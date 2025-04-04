import io

import pyotp
import qrcode
from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from starlette import status
from starlette.responses import StreamingResponse

from jwt_auth.validation import validate_auth_user
from totp_auth.utils import validate_user_for_qrcode
from users.schemas import ReadUserSchema, LoginTotpUserSchema
from config import APP_NAME

totp_auth_router = APIRouter(
    prefix='/auth-totp',
    tags=['Auth TOTP'],
)


@totp_auth_router.post("/qrcode", )
def get_qrcode(user: LoginTotpUserSchema = Depends(validate_user_for_qrcode)):
    totp = pyotp.TOTP(user.totp_secret)
    otp_url = totp.provisioning_uri(name=user.username, issuer_name=APP_NAME)

    qr = qrcode.make(otp_url)
    buf = io.BytesIO()
    qr.save(buf, format="PNG")
    buf.seek(0)

    return StreamingResponse(buf, media_type="image/png", status_code=status.HTTP_201_CREATED)

