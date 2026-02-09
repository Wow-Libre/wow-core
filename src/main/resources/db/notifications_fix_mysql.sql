-- Corrección para MySQL: la columna id debe ser AUTO_INCREMENT para que Hibernate (GenerationType.IDENTITY) funcione.
-- Ejecutar si obtienes: Field 'id' doesn't have a default value

ALTER TABLE platform.notifications
  MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
