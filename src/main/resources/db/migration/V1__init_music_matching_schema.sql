create table user_account (
                              id bigserial primary key,
                              username varchar(100) not null,
                              email varchar(255) not null,
                              created_at timestamptz not null default current_timestamp,

                              constraint uk_user_account_username unique (username),
                              constraint uk_user_account_email unique (email)
);

create table artist (
                        id bigserial primary key,
                        name varchar(255) not null,
                        external_source varchar(50),
                        external_id varchar(255),
                        created_at timestamptz not null default current_timestamp,

                        constraint uk_artist_source_external unique (external_source, external_id)
);

create table genre (
                       id bigserial primary key,
                       name varchar(100) not null,

                       constraint uk_genre_name unique (name)
);

create table track (
                       id bigserial primary key,
                       title varchar(255) not null,
                       artist_id bigint not null,
                       popularity integer not null default 0,
                       external_source varchar(50),
                       external_id varchar(255),
                       created_at timestamptz not null default current_timestamp,

                       constraint fk_track_artist
                           foreign key (artist_id) references artist (id),

                       constraint ck_track_popularity
                           check (popularity >= 0),

                       constraint uk_track_source_external unique (external_source, external_id)
);

create table track_genre (
                             track_id bigint not null,
                             genre_id bigint not null,

                             primary key (track_id, genre_id),

                             constraint fk_track_genre_track
                                 foreign key (track_id) references track (id) on delete cascade,

                             constraint fk_track_genre_genre
                                 foreign key (genre_id) references genre (id) on delete cascade
);

create table user_track_preference (
                                       user_id bigint not null,
                                       track_id bigint not null,
                                       preference_type varchar(20) not null,
                                       weight numeric(6,3) not null default 1.000,
                                       created_at timestamptz not null default current_timestamp,

                                       primary key (user_id, track_id),

                                       constraint fk_user_track_preference_user
                                           foreign key (user_id) references user_account (id) on delete cascade,

                                       constraint fk_user_track_preference_track
                                           foreign key (track_id) references track (id) on delete cascade,

                                       constraint ck_user_track_preference_type
                                           check (preference_type in ('FAVORITE', 'LIKE')),

                                       constraint ck_user_track_preference_weight
                                           check (weight > 0)
);

create table user_artist_preference (
                                        user_id bigint not null,
                                        artist_id bigint not null,
                                        weight numeric(6,3) not null default 1.000,
                                        created_at timestamptz not null default current_timestamp,

                                        primary key (user_id, artist_id),

                                        constraint fk_user_artist_preference_user
                                            foreign key (user_id) references user_account (id) on delete cascade,

                                        constraint fk_user_artist_preference_artist
                                            foreign key (artist_id) references artist (id) on delete cascade,

                                        constraint ck_user_artist_preference_weight
                                            check (weight > 0)
);

create table user_genre_preference (
                                       user_id bigint not null,
                                       genre_id bigint not null,
                                       weight numeric(6,3) not null default 1.000,
                                       created_at timestamptz not null default current_timestamp,

                                       primary key (user_id, genre_id),

                                       constraint fk_user_genre_preference_user
                                           foreign key (user_id) references user_account (id) on delete cascade,

                                       constraint fk_user_genre_preference_genre
                                           foreign key (genre_id) references genre (id) on delete cascade,

                                       constraint ck_user_genre_preference_weight
                                           check (weight > 0)
);

create index idx_track_artist_id on track (artist_id);

create index idx_track_genre_genre_id on track_genre (genre_id);

create index idx_user_track_preference_track_id on user_track_preference (track_id);

create index idx_user_artist_preference_artist_id on user_artist_preference (artist_id);

create index idx_user_genre_preference_genre_id on user_genre_preference (genre_id);