import logging
from datetime import datetime

from fastapi import APIRouter, Depends
from fastapi.security import HTTPBearer
from pydantic import BaseModel
from starlette.requests import Request

from config import auth_jwt, configure_logging
from jwt_auth.helpers import create_access_token, create_refresh_token, fetch_jwt_token
from jwt_auth.utils import decode_jwt

from jwt_auth.validation import get_current_user_4_refresh, get_current_active_user, validate_auth_user
from users.schemas import ReadUserSchema

from fastapi import Response

logger = logging.getLogger(__name__)
configure_logging()

http_bearer = HTTPBearer(auto_error=False)

jwt_auth_router = APIRouter(
    prefix='/auth-jwt',
    tags=['Auth JWT'],
    dependencies=[Depends(http_bearer)]
)


class TokenInfo(BaseModel):
    access_token: str
    refresh_token: str | None = None
    token_type: str = 'Bearer'


@jwt_auth_router.post('/login', response_model=TokenInfo)
async def login(response: Response, user: ReadUserSchema = Depends(validate_auth_user)):
    access_token = await create_access_token(user)
    refresh_token = await create_refresh_token(user)
    response.set_cookie(key=auth_jwt.access_token_name,
                        value=access_token,
                        samesite='none',
                        httponly=True,
                        secure=True,
                        max_age=int(auth_jwt.access_token_expires_in.total_seconds()),
                        )
    response.set_cookie(key=auth_jwt.refresh_token_name,
                        value=refresh_token,
                        samesite='none',
                        httponly=True,
                        secure=True,
                        max_age=int(auth_jwt.refresh_token_expires_in.total_seconds()),
                        )
    return TokenInfo(
        access_token=access_token,
        refresh_token=refresh_token
    )


@jwt_auth_router.post('/refresh', response_model=TokenInfo, response_model_exclude_none=True)
async def refresh(response: Response, request: Request, user: ReadUserSchema = Depends(get_current_user_4_refresh)):
    access_token = await create_access_token(user)

    refresh_token = fetch_jwt_token(request, auth_jwt.refresh_token_name)
    decoded_refresh_token = decode_jwt(refresh_token)
    current_time = datetime.utcnow()
    exp_time = datetime.fromtimestamp(decoded_refresh_token['exp'])
    time_diff = exp_time - current_time
    if time_diff.days <= auth_jwt.days_left_to_update:
        refresh_token = await create_refresh_token(user)
        response.set_cookie(key=auth_jwt.refresh_token_name,
                            value=refresh_token,
                            samesite='none',
                            httponly=True,
                            secure=True,
                            max_age=int(auth_jwt.refresh_token_expires_in.total_seconds()),
                            )
        logger.info('Refresh token was updated due to %d days left until expiration', time_diff.days)

    response.set_cookie(key=auth_jwt.access_token_name,
                        value=access_token,
                        samesite='none',
                        httponly=True,
                        secure=True,
                        max_age=int(auth_jwt.access_token_expires_in.total_seconds()),
                        )

    logger.info("New access token: %s", access_token)
    return TokenInfo(
        access_token=access_token,
    )


@jwt_auth_router.get('/user-info', )
def user_info(user: ReadUserSchema = Depends(get_current_active_user)) -> ReadUserSchema:
    return user
