import logging
import uuid

from sqlalchemy import select, insert, desc, update, delete
from sqlalchemy.ext.asyncio import AsyncSession

from config import USER_PAGE_SIZE, configure_logging
from users.models import UserModel
from users.schemas import UpdateUserSchema

logger = logging.getLogger(__name__)
configure_logging()


async def get_user(session: AsyncSession, uuid_: int | None = None, username: str | None = None) -> UserModel | None:
    if uuid_ is not None:
        query = (select(UserModel)
                 .where(UserModel.__table__.c.uuid == uuid_)
                 )
    elif username is not None:
        query = (select(UserModel)
                 .where(UserModel.__table__.c.username == username)
                 )
    else:
        raise ValueError("Please provide either an uuid or username")
    result = await session.execute(query)
    await session.commit()
    user = result.scalars().first()
    return user


async def username_is_unique(username: str, session: AsyncSession):
    user = await get_user(username=username, session=session)
    if user is None:
        return True
    return False


async def create_user(user_data: dict, session: AsyncSession):
    statement = insert(UserModel).values({**user_data, 'uuid': uuid.uuid4()}).returning(
        UserModel.uuid, UserModel.username, UserModel.created_at, UserModel.email, UserModel.is_active
    )
    result = await session.execute(statement)
    created_data = result.first()
    await session.commit()
    logger.info("The user #%d was successfully created", created_data[0])
    return created_data


async def get_users(page: int, session: AsyncSession):
    query = select(UserModel).limit(USER_PAGE_SIZE)
    if page:
        query = query.offset(page * USER_PAGE_SIZE)
    query = query.order_by(desc(UserModel.__table__.c.created_at))
    result = await session.execute(query)
    await session.commit()
    users = result.scalars().all()
    return users


async def update_user(username: str, params: UpdateUserSchema, session: AsyncSession):
    statement = (update(UserModel)
                 .where(UserModel.__table__.c.username == username)
                 .values(**params.model_dump(exclude_none=True))
                 )
    await session.execute(statement)
    await session.commit()


async def delete_user(username: str, session: AsyncSession):
    statement = (delete(UserModel)
                 .where(UserModel.__table__.c.username == username)
                 )
    await session.execute(statement)
    await session.commit()
