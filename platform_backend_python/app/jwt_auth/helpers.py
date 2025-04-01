from datetime import timedelta

from config import auth_jwt, configure_logging
from jwt_auth.utils import encode_jwt
from users.schemas import ReadUserSchema
from starlette.requests import Request
from fastapi import status, HTTPException

import logging

TOKEN_TYPE_FIELD = 'type'
ACCESS_TOKEN_TYPE = 'access'
REFRESH_TOKEN_TYPE = 'refresh'

logger = logging.getLogger(__name__)
configure_logging()


def create_jwt(
        token_type: str,
        token_data: dict,
        expires_in: timedelta,
) -> str:
    jwt_payload = {TOKEN_TYPE_FIELD: token_type}
    jwt_payload.update(token_data)
    return encode_jwt(payload=jwt_payload, expires_in=expires_in)


async def create_access_token(user: ReadUserSchema) -> str:
    jwt_payload = {
        # 'uuid' should be in 'sub'
        'sub': str(user.uuid),
        'username': user.username,
        'email': user.email,
    }
    return create_jwt(
        token_type=ACCESS_TOKEN_TYPE,
        token_data=jwt_payload,
        expires_in=auth_jwt.access_token_expires_in,
    )


async def create_refresh_token(user: ReadUserSchema) -> str:
    jwt_payload = {
        'sub': str(user.uuid),
    }
    return create_jwt(
        token_type=REFRESH_TOKEN_TYPE,
        token_data=jwt_payload,
        expires_in=auth_jwt.refresh_token_expires_in,
    )


def fetch_jwt_token(request: Request, token_type: str) -> str:

    if token_type != auth_jwt.access_token_name and token_type != auth_jwt.refresh_token_name:
        raise HTTPException(detail='invalid token type', status_code=status.HTTP_401_UNAUTHORIZED)

    token_from_cookies = request.cookies.get(token_type)
    token = token_from_cookies
    if token_from_cookies is None:
        try:
            token_from_header = request.headers.get('Authorization')[7::]  # "Bearer <token>"
            token = token_from_header
        except Exception as e:
            logger.info("Error fetching token: %s", e)
            logger.info("Follow this format Header \"Authorization\": \"Bearer <token>\".")
            raise HTTPException(detail='invalid token', status_code=status.HTTP_401_UNAUTHORIZED)
    return token
