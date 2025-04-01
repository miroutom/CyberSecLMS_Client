import pyotp
from fastapi import Depends
from sqlalchemy.ext.asyncio import AsyncSession

from database.db_sessions import get_async_session
from jwt_auth.utils import validate_password
from users.crud import get_user
from users.models import UserModel
from users.schemas import LoginTotpUserSchema, ReadUserSchema
from utils.exceptions import unauthorized_exception


def generate_totp_secret() -> str:
    return pyotp.random_base32()


def validate_totp_secret(user: UserModel, security_code: str) -> bool:
    totp = pyotp.TOTP(user.totp_secret)
    return totp.verify(security_code)


async def validate_user_for_qrcode(user_data: LoginTotpUserSchema,
                                   session: AsyncSession = Depends(get_async_session)) -> ReadUserSchema:

    user = await get_user(username=user_data.username, session=session)

    if user is None or not validate_password(
            password=user_data.password,
            hashed_password=user.password,
    ):
        raise unauthorized_exception

    return user


