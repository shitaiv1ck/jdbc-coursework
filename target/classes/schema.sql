drop table if exists bags, results, tests, tickets, astrounauts, capsules, foods, spaceships, flights;

create table flights(
    id INT primary key generated always as identity,
    destination VARCHAR(500),
    flight_date timestamptz,
    arrive_date timestamptz,
    constraint correct_date check (
        (flight_date is null and arrive_date is null)
            or
        (flight_date is not null and arrive_date is null)
            or
        (flight_date < arrive_date)
        )
);

create table spaceships(
    id int primary key generated always as identity,
    id_flight int references flights(id) not null,
    capsules_count INT check(capsules_count >= 0)
);

create table foods(
    id INT primary key generated always as identity,
    id_ships int references spaceships(id),
    contents text
);
create table capsules(
    id INT primary key generated always as identity,
    id_ships int references spaceships(id),
    class VARCHAR(100) check (class in ('эконом', 'панорамный люкс'))
);

create table astrounauts(
    id INT primary key generated always as identity,
    id_capsule int references capsules(id),
    role varchar(100) default 'пассажир',
    weight decimal not null check (weight > 0),
    height int not null check (height > 0)
);

create table tickets(
    id INT primary key generated always as identity,
    id_astrounaut int references astrounauts(id) on update cascade,
    id_flight int not null,
    price numeric(10, 2) check (price >= 0),
    constraint fk_id_fligth foreign key (id_flight) references flights(id) on delete cascade
);

create table tests(
    id INT primary key generated always as identity,
    topic varchar(150) not null unique
);

create table results(
    id_test int references tests(id) not null,
    id_astrounaut int references astrounauts(id) not null,
    completed bool not null,
    constraint pk_results primary key(id_test, id_astrounaut)
);

create table bags(
    id int primary key generated always as identity,
    id_astrounaut int references astrounauts(id) not null,
    flight_weight decimal check (flight_weight > 0.0)
);

