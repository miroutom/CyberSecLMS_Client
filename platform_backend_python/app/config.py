from datetime import timedelta
from pathlib import Path
from typing import Final

from dotenv import load_dotenv
import os

from pydantic import BaseModel
import logging

USER_PAGE_SIZE: Final = 16

load_dotenv()

DB_HOST: Final = os.getenv("DB_HOST")
DB_USER: Final = os.getenv("DB_USER")
DB_NAME: Final = os.getenv("DB_NAME")
DB_PASS: Final = os.getenv("DB_PASS")
DB_PORT: Final = os.getenv("DB_PORT")

DB_URL: Final = f"postgresql+asyncpg://{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

CERTIFICATIONS_DIR: Final = Path('certs')

APP_NAME = "CyberSecurityPlatform"


class AuthJWT(BaseModel):
    private_key_path: Final[Path] = CERTIFICATIONS_DIR / "jwt-private.pem"
    public_key_path: Final[Path] = CERTIFICATIONS_DIR / "jwt-public.pem"
    algorithm: Final[str] = "RS256"
    access_token_expires_in: timedelta = timedelta(minutes=60)
    refresh_token_expires_in: timedelta = timedelta(days=60)
    access_token_name: str = "access_token"
    refresh_token_name: str = "refresh_token"
    days_left_to_update: Final[int] = 30


auth_jwt: AuthJWT = AuthJWT()


def configure_logging(level: int = logging.INFO) -> None:
    logging.basicConfig(
        level=level,
        datefmt="%Y-%m-%d %H:%M:%S",
        format="%(levelname)-9s %(message)r %(funcName)s() in <%(module)s:%(lineno)3d> at [%(asctime)s.%(msecs)03d]"
    )