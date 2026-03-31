insert into user_account (username, email) values
                                               ('alex_indie', 'alex_indie@example.com'),
                                               ('nina_synth', 'nina_synth@example.com'),
                                               ('mike_mix', 'mike_mix@example.com'),
                                               ('dan_hiphop', 'dan_hiphop@example.com'),
                                               ('eva_chill', 'eva_chill@example.com');

insert into user_track_preference (user_id, track_id, preference_type, weight)
values
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from track where external_source = 'seed' and external_id = 'track_505'),
        'FAVORITE',
        1.500
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from track where external_source = 'seed' and external_id = 'track_the_less_i_know_the_better'),
        'FAVORITE',
        1.450
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from track where external_source = 'seed' and external_id = 'track_reptilia'),
        'LIKE',
        1.000
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from track where external_source = 'seed' and external_id = 'track_the_adults_are_talking'),
        'LIKE',
        0.950
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from track where external_source = 'seed' and external_id = 'track_show_me_how'),
        'LIKE',
        0.900
    ),

    (
        (select id from user_account where username = 'nina_synth'),
        (select id from track where external_source = 'seed' and external_id = 'track_blinding_lights'),
        'FAVORITE',
        1.500
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from track where external_source = 'seed' and external_id = 'track_midnight_city'),
        'FAVORITE',
        1.400
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from track where external_source = 'seed' and external_id = 'track_borderline'),
        'LIKE',
        1.000
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from track where external_source = 'seed' and external_id = 'track_get_lucky'),
        'LIKE',
        0.950
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from track where external_source = 'seed' and external_id = 'track_instant_crush'),
        'LIKE',
        0.900
    ),

    (
        (select id from user_account where username = 'mike_mix'),
        (select id from track where external_source = 'seed' and external_id = 'track_the_less_i_know_the_better'),
        'FAVORITE',
        1.500
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from track where external_source = 'seed' and external_id = 'track_blinding_lights'),
        'FAVORITE',
        1.450
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from track where external_source = 'seed' and external_id = 'track_505'),
        'LIKE',
        1.000
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from track where external_source = 'seed' and external_id = 'track_midnight_city'),
        'LIKE',
        0.950
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from track where external_source = 'seed' and external_id = 'track_borderline'),
        'LIKE',
        0.900
    ),

    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from track where external_source = 'seed' and external_id = 'track_humble'),
        'FAVORITE',
        1.500
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from track where external_source = 'seed' and external_id = 'track_nights'),
        'FAVORITE',
        1.400
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from track where external_source = 'seed' and external_id = 'track_money_trees'),
        'LIKE',
        1.000
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from track where external_source = 'seed' and external_id = 'track_pink_white'),
        'LIKE',
        0.950
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from track where external_source = 'seed' and external_id = 'track_starboy'),
        'LIKE',
        0.900
    ),

    (
        (select id from user_account where username = 'eva_chill'),
        (select id from track where external_source = 'seed' and external_id = 'track_show_me_how'),
        'FAVORITE',
        1.500
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from track where external_source = 'seed' and external_id = 'track_kerala'),
        'FAVORITE',
        1.400
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from track where external_source = 'seed' and external_id = 'track_lauren'),
        'LIKE',
        1.000
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from track where external_source = 'seed' and external_id = 'track_cirrus'),
        'LIKE',
        0.950
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from track where external_source = 'seed' and external_id = 'track_wait'),
        'LIKE',
        0.900
    );

insert into user_artist_preference (user_id, artist_id, weight)
values
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_arctic_monkeys'),
        1.300
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_tame_impala'),
        1.250
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_strokes'),
        1.150
    ),

    (
        (select id from user_account where username = 'nina_synth'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_weeknd'),
        1.300
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_m83'),
        1.250
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_daft_punk'),
        1.200
    ),

    (
        (select id from user_account where username = 'mike_mix'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_tame_impala'),
        1.300
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_weeknd'),
        1.250
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_arctic_monkeys'),
        1.100
    ),

    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_kendrick_lamar'),
        1.300
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_frank_ocean'),
        1.250
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_weeknd'),
        1.100
    ),

    (
        (select id from user_account where username = 'eva_chill'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_men_i_trust'),
        1.300
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_bonobo'),
        1.250
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from artist where external_source = 'seed' and external_id = 'artist_m83'),
        1.100
    );

insert into user_genre_preference (user_id, genre_id, weight)
values
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from genre where name = 'Indie Rock'),
        1.200
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from genre where name = 'Alternative Rock'),
        1.150
    ),
    (
        (select id from user_account where username = 'alex_indie'),
        (select id from genre where name = 'Lo-fi'),
        0.900
    ),

    (
        (select id from user_account where username = 'nina_synth'),
        (select id from genre where name = 'Synthpop'),
        1.200
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from genre where name = 'Pop'),
        1.100
    ),
    (
        (select id from user_account where username = 'nina_synth'),
        (select id from genre where name = 'Electronic'),
        1.000
    ),

    (
        (select id from user_account where username = 'mike_mix'),
        (select id from genre where name = 'Indie Rock'),
        1.150
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from genre where name = 'Synthpop'),
        1.100
    ),
    (
        (select id from user_account where username = 'mike_mix'),
        (select id from genre where name = 'Pop'),
        1.000
    ),

    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from genre where name = 'Hip-Hop'),
        1.200
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from genre where name = 'R&B'),
        1.100
    ),
    (
        (select id from user_account where username = 'dan_hiphop'),
        (select id from genre where name = 'Pop'),
        0.900
    ),

    (
        (select id from user_account where username = 'eva_chill'),
        (select id from genre where name = 'Lo-fi'),
        1.200
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from genre where name = 'Electronic'),
        1.100
    ),
    (
        (select id from user_account where username = 'eva_chill'),
        (select id from genre where name = 'House'),
        1.000
    );