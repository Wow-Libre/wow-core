-- MySQL: id debe ser AUTO_INCREMENT para que Hibernate (GenerationType.IDENTITY) asigne el valor.
CREATE TABLE IF NOT EXISTS platform.notifications (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    message TEXT,
    created_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS platform.notification_read (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    notification_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    UNIQUE KEY uk_notification_user (notification_id, user_id),
    CONSTRAINT fk_notification_read_notification
        FOREIGN KEY (notification_id) REFERENCES platform.notifications(id) ON DELETE CASCADE
);
