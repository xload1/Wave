alter table user_track_preference
    drop constraint ck_user_track_preference_weight,
    drop column weight;

alter table user_artist_preference
    drop constraint ck_user_artist_preference_weight,
    drop column weight;

alter table user_genre_preference
    drop constraint ck_user_genre_preference_weight,
    drop column weight;