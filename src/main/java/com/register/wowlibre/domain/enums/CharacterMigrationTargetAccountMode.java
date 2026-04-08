package com.register.wowlibre.domain.enums;

/**
 * How the migrated character should be attached on approval: new game account vs an existing one.
 */
public enum CharacterMigrationTargetAccountMode {
    /** Create a new game account using {@code target_game_account_username}. */
    CREATE_NEW,
    /** Attach to an existing game account identified by {@code target_existing_account_id} (emulator account id). */
    USE_EXISTING
}
