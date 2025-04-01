
from fastapi import APIRouter, status, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from database.db_sessions import get_async_session
from jwt_auth.validation import get_current_active_user
from totp_auth.utils import generate_totp_secret
from users.crud import get_user, create_user, username_is_unique, get_users, update_user, delete_user
from users.schemas import ReadUserSchema, CreateUserSchema, UpdateUserSchema, BrowseUserSchema

users_router = APIRouter(
    prefix='/user',
    tags=['Users'],
)


@users_router.post("",
                   status_code=status.HTTP_201_CREATED,
                   response_model=ReadUserSchema,
                   response_model_exclude_none=True,
                   )
async def create_user_view(user: CreateUserSchema, session: AsyncSession = Depends(get_async_session)):
    user_data = user.model_dump(include={'username', 'password1', 'email', })
    if not await username_is_unique(username := user_data['username'], session=session):
        raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST,
                            detail=f'User with the {username = } already exists.')

    user_data['password'] = user_data.pop('password1')
    user_data['totp_secret'] = generate_totp_secret()
    return await create_user(user_data=user_data, session=session)


@users_router.get("/{username}", response_model=BrowseUserSchema)
async def get_user_view(username: str, session: AsyncSession = Depends(get_async_session)):
    user = await get_user(username=username, session=session)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                            detail=f'User with the {username = } does not exist.')
    return user


@users_router.get("", response_model=list[BrowseUserSchema])
async def get_users_view(page: int | None = None, session: AsyncSession = Depends(get_async_session)):
    return await get_users(page=page, session=session)


@users_router.patch("/{username}", status_code=status.HTTP_204_NO_CONTENT)
async def update_user_view(username: str,
                           params: UpdateUserSchema,
                           session: AsyncSession = Depends(get_async_session),
                           user: ReadUserSchema = Depends(get_current_active_user)):
    if username != user.username:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=f'Not authorized to update')
    await update_user(username=username, params=params, session=session)


@users_router.delete("/{username}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_user_view(username: str,
                           session: AsyncSession = Depends(get_async_session),
                           user: ReadUserSchema = Depends(get_current_active_user)):
    if username != user.username:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail=f'Not authorized to delete')
    await delete_user(username=username, session=session)
