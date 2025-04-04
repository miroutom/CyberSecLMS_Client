import uuid
from datetime import datetime, timedelta

import jwt
import bcrypt
import config


def encode_jwt(payload: dict,
               private_key: str = config.auth_jwt.private_key_path.read_text(),
               algorithm: str = config.auth_jwt.algorithm,
               expires_in: timedelta = config.auth_jwt.access_token_expires_in,
               ):
    upd_payload = payload.copy()
    current_time = datetime.utcnow()
    upd_payload.update(
        exp=current_time + expires_in,
        iat=current_time,
        jti=str(uuid.uuid4()),
    )
    encoded = jwt.encode(upd_payload, private_key, algorithm=algorithm)
    return encoded


def decode_jwt(token: str | bytes,
               public_key: str = config.auth_jwt.public_key_path.read_text(),
               algorithm: str = config.auth_jwt.algorithm):
    decoded = jwt.decode(token, public_key, algorithms=[algorithm])
    return decoded


def hash_password(password: str) -> bytes:
    return bcrypt.hashpw(password.encode(), bcrypt.gensalt())


def validate_password(password: str, hashed_password: bytes) -> bool:
    return bcrypt.checkpw(password.encode(), hashed_password)