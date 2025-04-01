from sqlalchemy import Column, TIMESTAMP, text, DateTime
from sqlalchemy.orm import declarative_base


class BaseTable:
    __abstract__ = True
    created_at = Column(TIMESTAMP(timezone=True), server_default=text('now()'))
    updated_at = Column(DateTime(timezone=True), onupdate=text('now()'))


Base = declarative_base(cls=BaseTable)
