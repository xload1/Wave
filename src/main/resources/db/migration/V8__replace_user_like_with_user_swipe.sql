create table user_swipe (
                            id bigserial primary key,
                            from_user_id bigint not null,
                            to_user_id bigint not null,
                            reaction_type varchar(20) not null,
                            created_at timestamptz not null default current_timestamp,

                            constraint fk_user_swipe_from_user
                                foreign key (from_user_id) references user_account (id) on delete cascade,

                            constraint fk_user_swipe_to_user
                                foreign key (to_user_id) references user_account (id) on delete cascade,

                            constraint uk_user_swipe_pair
                                unique (from_user_id, to_user_id),

                            constraint ck_user_swipe_not_self
                                check (from_user_id <> to_user_id),

                            constraint ck_user_swipe_reaction_type
                                check (reaction_type in ('LIKE', 'PASS'))
);

insert into user_swipe (from_user_id, to_user_id, reaction_type, created_at)
select from_user_id, to_user_id, 'LIKE', created_at
from user_like;

drop table user_like;

create index idx_user_swipe_from_user_id on user_swipe (from_user_id);
create index idx_user_swipe_to_user_id on user_swipe (to_user_id);
create index idx_user_swipe_reaction_type on user_swipe (reaction_type);