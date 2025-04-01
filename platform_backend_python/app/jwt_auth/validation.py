from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from jwt import InvalidTokenError
from sqlalchemy.ext.asyncio import AsyncSession
from starlette import status
from starlette.requests import Request

from config import auth_jwt
from database.db_sessions import get_async_session
from jwt_auth.helpers import TOKEN_TYPE_FIELD, ACCESS_TOKEN_TYPE, REFRESH_TOKEN_TYPE, fetch_jwt_token
from jwt_auth.utils import decode_jwt, validate_password
from totp_auth.utils import validate_totp_secret
from users.crud import get_user
from users.schemas import ReadUserSchema, LoginJwtUserSchema

import logging
from config import configure_logging
from utils.exceptions import unauthorized_exception

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/v1/auth-jwt/login")

logger = logging.getLogger(__name__)
configure_logging()


async def get_access_token_payload(request: Request) -> ReadUserSchema:
    access_token = fetch_jwt_token(request, auth_jwt.access_token_name)
    logger.info("Access token: %s", access_token)

    try:
        payload = decode_jwt(access_token)
    except InvalidTokenError:
        raise HTTPException(detail='invalid token', status_code=status.HTTP_401_UNAUTHORIZED)
    return payload


async def get_refresh_token_payload(request: Request) -> ReadUserSchema:
    refresh_token = fetch_jwt_token(request, auth_jwt.refresh_token_name)
    logger.info("Refresh token: %s", refresh_token)

    try:
        payload = decode_jwt(refresh_token)
    except InvalidTokenError:
        raise HTTPException(detail='invalid token', status_code=status.HTTP_401_UNAUTHORIZED)
    return payload


async def validate_token_type(payload: dict, token_type: str) -> bool:
    current_token_type = payload.get(TOKEN_TYPE_FIELD)
    if current_token_type == token_type:
        return True
    raise HTTPException(detail=f'Invalid token type {current_token_type!r} expected {token_type!r}',
                        status_code=status.HTTP_401_UNAUTHORIZED)


async def get_auth_user_from_token(token_type: str, payload: dict,
                                   session: AsyncSession) -> ReadUserSchema:
    await validate_token_type(payload, token_type)
    uuid_ = payload.get('sub')
    if not (user := await get_user(uuid_=uuid_, session=session)):
        raise HTTPException(detail='invalid token', status_code=status.HTTP_401_UNAUTHORIZED)
    return user


async def get_current_user(token_type: str = ACCESS_TOKEN_TYPE,
                           payload: dict = Depends(get_access_token_payload),
                           session: AsyncSession = Depends(get_async_session)):
    return await get_auth_user_from_token(token_type, payload, session)


async def get_current_user_4_refresh(token_type: str = REFRESH_TOKEN_TYPE,
                                     payload: dict = Depends(get_refresh_token_payload),
                                     session: AsyncSession = Depends(get_async_session)):
    return await get_auth_user_from_token(token_type, payload, session)


async def validate_auth_user(user_data: LoginJwtUserSchema,
                             session: AsyncSession = Depends(get_async_session)) -> ReadUserSchema:

    user = await get_user(username=user_data.username, session=session)

    if user is None or not validate_password(
            password=user_data.password,
            hashed_password=user.password,
    ):
        raise unauthorized_exception

    if not user.is_active:
        raise HTTPException(detail='Inactive user', status_code=status.HTTP_403_FORBIDDEN)

    if not validate_totp_secret(user, user_data.security_code):
        raise unauthorized_exception

    return user


async def get_current_active_user(user: ReadUserSchema = Depends(get_current_user)) -> ReadUserSchema:
    if user.is_active:
        return user
    raise HTTPException(detail='inactive user', status_code=status.HTTP_403_FORBIDDEN)
