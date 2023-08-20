create table recipe(
    id serial bigint not null,
    name varchar(64) not null,
    type varchar(8) not null,
    instructions text not null,
    ingredients text not null,
    primary key(id),
    check (type in ('Breakfast', 'Dinner', 'Lunch'))
);