
from sqlalchemy.ext.asyncio import async_sessionmaker, AsyncSession, create_async_engine

from config import DB_URL

async_engine = create_async_engine(DB_URL)
async_session_maker = async_sessionmaker(async_engine, class_=AsyncSession, expire_on_commit=False)


async def get_async_session():
    async with async_session_maker() as session:
        yield session
