-- JSAAVEDR 20201214
-- Scripts para la creación de artefactos de base de datos
-- Para pruebas JDBC
set MODE Oracle;

-- creando secuencia que utiliza
-- la entidad Maetro
create SEQUENCE if not EXISTS SA_FOL_FIC
  MINVALUE 3
  MAXVALUE 9999999999999999
  start with 3
  INCREMENT by 100
  CACHE 100;

--Creando entidad maestro
create table if not EXISTS TGLO_MAESTRO
(
    ID bigint not null PRIMARY KEY,
    NOM varchar(255),
    DES varchar(255),
    TIP int
);

--creando entidad detalle
create table if not EXISTS TGLO_DETALLE
(
    ID bigint not null PRIMARY KEY,
    FEC date,
    CNT bigint,
    MNT number(21,4),
    ID_MAE bigint
);
