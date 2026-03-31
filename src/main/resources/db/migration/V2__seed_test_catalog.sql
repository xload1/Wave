insert into genre (name) values
                             ('Alternative Rock'),
                             ('Indie Rock'),
                             ('Synthpop'),
                             ('Pop'),
                             ('Hip-Hop'),
                             ('R&B'),
                             ('Electronic'),
                             ('House'),
                             ('Jazz'),
                             ('Lo-fi');

insert into artist (name, external_source, external_id) values
                                                            ('Arctic Monkeys', 'seed', 'artist_arctic_monkeys'),
                                                            ('The Strokes', 'seed', 'artist_the_strokes'),
                                                            ('Tame Impala', 'seed', 'artist_tame_impala'),
                                                            ('The Weeknd', 'seed', 'artist_the_weeknd'),
                                                            ('Daft Punk', 'seed', 'artist_daft_punk'),
                                                            ('Kendrick Lamar', 'seed', 'artist_kendrick_lamar'),
                                                            ('Frank Ocean', 'seed', 'artist_frank_ocean'),
                                                            ('M83', 'seed', 'artist_m83'),
                                                            ('Bonobo', 'seed', 'artist_bonobo'),
                                                            ('Men I Trust', 'seed', 'artist_men_i_trust');

insert into track (title, artist_id, popularity, external_source, external_id)
values
    (
        'Do I Wanna Know?',
        (select id from artist where external_source = 'seed' and external_id = 'artist_arctic_monkeys'),
        92,
        'seed',
        'track_do_i_wanna_know'
    ),
    (
        '505',
        (select id from artist where external_source = 'seed' and external_id = 'artist_arctic_monkeys'),
        85,
        'seed',
        'track_505'
    ),
    (
        'Reptilia',
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_strokes'),
        87,
        'seed',
        'track_reptilia'
    ),
    (
        'The Adults Are Talking',
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_strokes'),
        82,
        'seed',
        'track_the_adults_are_talking'
    ),
    (
        'The Less I Know The Better',
        (select id from artist where external_source = 'seed' and external_id = 'artist_tame_impala'),
        91,
        'seed',
        'track_the_less_i_know_the_better'
    ),
    (
        'Borderline',
        (select id from artist where external_source = 'seed' and external_id = 'artist_tame_impala'),
        80,
        'seed',
        'track_borderline'
    ),
    (
        'Blinding Lights',
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_weeknd'),
        98,
        'seed',
        'track_blinding_lights'
    ),
    (
        'Starboy',
        (select id from artist where external_source = 'seed' and external_id = 'artist_the_weeknd'),
        93,
        'seed',
        'track_starboy'
    ),
    (
        'Get Lucky',
        (select id from artist where external_source = 'seed' and external_id = 'artist_daft_punk'),
        95,
        'seed',
        'track_get_lucky'
    ),
    (
        'Instant Crush',
        (select id from artist where external_source = 'seed' and external_id = 'artist_daft_punk'),
        84,
        'seed',
        'track_instant_crush'
    ),
    (
        'HUMBLE.',
        (select id from artist where external_source = 'seed' and external_id = 'artist_kendrick_lamar'),
        90,
        'seed',
        'track_humble'
    ),
    (
        'Money Trees',
        (select id from artist where external_source = 'seed' and external_id = 'artist_kendrick_lamar'),
        88,
        'seed',
        'track_money_trees'
    ),
    (
        'Pink + White',
        (select id from artist where external_source = 'seed' and external_id = 'artist_frank_ocean'),
        83,
        'seed',
        'track_pink_white'
    ),
    (
        'Nights',
        (select id from artist where external_source = 'seed' and external_id = 'artist_frank_ocean'),
        86,
        'seed',
        'track_nights'
    ),
    (
        'Midnight City',
        (select id from artist where external_source = 'seed' and external_id = 'artist_m83'),
        85,
        'seed',
        'track_midnight_city'
    ),
    (
        'Wait',
        (select id from artist where external_source = 'seed' and external_id = 'artist_m83'),
        68,
        'seed',
        'track_wait'
    ),
    (
        'Kerala',
        (select id from artist where external_source = 'seed' and external_id = 'artist_bonobo'),
        70,
        'seed',
        'track_kerala'
    ),
    (
        'Cirrus',
        (select id from artist where external_source = 'seed' and external_id = 'artist_bonobo'),
        72,
        'seed',
        'track_cirrus'
    ),
    (
        'Show Me How',
        (select id from artist where external_source = 'seed' and external_id = 'artist_men_i_trust'),
        76,
        'seed',
        'track_show_me_how'
    ),
    (
        'Lauren',
        (select id from artist where external_source = 'seed' and external_id = 'artist_men_i_trust'),
        73,
        'seed',
        'track_lauren'
    );

insert into track_genre (track_id, genre_id)
values
    (
        (select id from track where external_source = 'seed' and external_id = 'track_do_i_wanna_know'),
        (select id from genre where name = 'Alternative Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_do_i_wanna_know'),
        (select id from genre where name = 'Indie Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_505'),
        (select id from genre where name = 'Alternative Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_505'),
        (select id from genre where name = 'Indie Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_reptilia'),
        (select id from genre where name = 'Alternative Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_reptilia'),
        (select id from genre where name = 'Indie Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_the_adults_are_talking'),
        (select id from genre where name = 'Indie Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_the_less_i_know_the_better'),
        (select id from genre where name = 'Indie Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_the_less_i_know_the_better'),
        (select id from genre where name = 'Pop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_borderline'),
        (select id from genre where name = 'Synthpop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_borderline'),
        (select id from genre where name = 'Pop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_blinding_lights'),
        (select id from genre where name = 'Synthpop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_blinding_lights'),
        (select id from genre where name = 'Pop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_starboy'),
        (select id from genre where name = 'Pop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_starboy'),
        (select id from genre where name = 'R&B')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_get_lucky'),
        (select id from genre where name = 'Pop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_get_lucky'),
        (select id from genre where name = 'Electronic')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_instant_crush'),
        (select id from genre where name = 'Electronic')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_humble'),
        (select id from genre where name = 'Hip-Hop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_money_trees'),
        (select id from genre where name = 'Hip-Hop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_pink_white'),
        (select id from genre where name = 'R&B')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_nights'),
        (select id from genre where name = 'R&B')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_midnight_city'),
        (select id from genre where name = 'Synthpop')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_wait'),
        (select id from genre where name = 'Electronic')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_kerala'),
        (select id from genre where name = 'Electronic')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_kerala'),
        (select id from genre where name = 'House')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_cirrus'),
        (select id from genre where name = 'Electronic')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_show_me_how'),
        (select id from genre where name = 'Lo-fi')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_show_me_how'),
        (select id from genre where name = 'Indie Rock')
    ),
    (
        (select id from track where external_source = 'seed' and external_id = 'track_lauren'),
        (select id from genre where name = 'Lo-fi')
    );