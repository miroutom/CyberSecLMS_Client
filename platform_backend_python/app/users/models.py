from sqlalchemy import Column, String, Boolean, LargeBinary, Integer, Uuid
from database.models import Base


class UserModel(Base):
    __tablename__ = 'auth_user'

    uuid = Column(Uuid, primary_key=True)
    username = Column(String(63), index=True, )
    password = Column(LargeBinary)
    totp_secret = Column(String(33))
    email = Column(String(63), nullable=True, )
    is_active = Column(Boolean, default=True, )
