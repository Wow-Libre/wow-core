# Notificaciones: destinatario y “me gusta” en el feed social

## Comportamiento

- La tabla `platform.notifications` admite **`recipient_user_id`** (nullable).
  - **`NULL`**: notificación **global** (la ven todos los usuarios; mismo comportamiento que antes).
  - **Valor**: notificación **dirigida**; solo ese `user_id` la ve en `GET /api/notifications`.
- Al añadir un **like** a una publicación (`POST /api/social/posts/{id}/like`), si el autor del post no es quien reacciona, se crea una fila en `notifications` con:
  - `title`: `Me gusta en tu publicación`
  - `message`: nombre del que reacciona + referencia al id del post.
  - `recipient_user_id`: autor del post.

No se borra la notificación si el usuario quita el like (MVP).

## Migración en bases ya existentes

Si la tabla `notifications` se creó **antes** de esta columna, ejecutar en MySQL:

```sql
ALTER TABLE platform.notifications
    ADD COLUMN recipient_user_id BIGINT NULL;

CREATE INDEX idx_notifications_recipient ON platform.notifications (recipient_user_id);
```

Las instalaciones nuevas tomarán la definición actualizada de `src/main/resources/db/scripts.sql`.

## CMS

El cliente ya consume `GET /api/notifications` con el JWT; no requiere cambios de contrato: las notificaciones dirigidas solo aparecen para el usuario destinatario.
