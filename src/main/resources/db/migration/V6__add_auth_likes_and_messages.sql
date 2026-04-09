alter table user_account
    add column password_hash varchar(255) not null;

create table user_like (
                           id bigserial primary key,
                           from_user_id bigint not null,
                           to_user_id bigint not null,
                           created_at timestamptz not null default current_timestamp,

                           constraint fk_user_like_from_user
                               foreign key (from_user_id) references user_account (id) on delete cascade,

                           constraint fk_user_like_to_user
                               foreign key (to_user_id) references user_account (id) on delete cascade,

                           constraint uk_user_like_pair
                               unique (from_user_id, to_user_id),

                           constraint ck_user_like_not_self
                               check (from_user_id <> to_user_id)
);

create index idx_user_like_from_user_id on user_like (from_user_id);
create index idx_user_like_to_user_id on user_like (to_user_id);

create table user_message (
                              id bigserial primary key,
                              from_user_id bigint not null,
                              to_user_id bigint not null,
                              message_ciphertext text not null,
                              message_nonce varchar(128) not null,
                              sent_at timestamptz not null default current_timestamp,

                              constraint fk_user_message_from_user
                                  foreign key (from_user_id) references user_account (id) on delete cascade,

                              constraint fk_user_message_to_user
                                  foreign key (to_user_id) references user_account (id) on delete cascade
);

create index idx_user_message_from_user_id on user_message (from_user_id);
create index idx_user_message_to_user_id on user_message (to_user_id);
create index idx_user_message_sent_at on user_message (sent_at);