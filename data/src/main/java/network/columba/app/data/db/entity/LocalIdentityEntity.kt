package network.columba.app.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a local Reticulum identity in the database.
 * Each identity has its own keypair and can have isolated data (contacts, conversations, messages).
 *
 * @property identityHash The 32-character hex hash of the identity (primary key)
 * @property displayName User-friendly name for the identity (e.g., "Work", "Personal")
 * @property destinationHash The LXMF destination hash for this identity
 * @property filePath Path to the identity file in app storage
 * @property keyData Raw 64-byte private key data (DEPRECATED: use encryptedKeyData)
 * @property encryptedKeyData AES-256-GCM encrypted key data using Android Keystore
 * @property keyEncryptionVersion Encryption version: 0=plain, 1=device-only, 2=device+password
 * @property passwordSalt Salt used for password-based key derivation (if password protected)
 * @property passwordVerificationHash Hash for verifying password correctness (if password protected)
 * @property createdTimestamp Unix timestamp when the identity was created
 * @property lastUsedTimestamp Unix timestamp when the identity was last active
 * @property isActive True if this is the currently active identity (only one should be active)
 */
@Entity(
    tableName = "local_identities",
    indices = [
        Index("lastUsedTimestamp"), // For ordering
        Index("isActive"), // For active identity queries
    ],
)
data class LocalIdentityEntity(
    @PrimaryKey
    val identityHash: String,
    val displayName: String,
    val destinationHash: String,
    val filePath: String,
    @Deprecated("Use encryptedKeyData instead - this field will be cleared after migration")
    val keyData: ByteArray? = null,
    val encryptedKeyData: ByteArray? = null,
    val keyEncryptionVersion: Int = 0, // 0=plain, 1=device, 2=device+password
    val passwordSalt: ByteArray? = null,
    val passwordVerificationHash: ByteArray? = null,
    val createdTimestamp: Long,
    val lastUsedTimestamp: Long,
    val isActive: Boolean,
    val iconName: String? = null,
    val iconForegroundColor: String? = null, // Hex RGB e.g., "FFFFFF"
    val iconBackgroundColor: String? = null, // Hex RGB e.g., "1E88E5"
    /**
     * Opt-in: do not enable LXMF delivery ratchets for this identity.
     *
     * Ratchets rotate a receiver-side key, so while one is active peers encrypt
     * to the ratchet instead of the destination's published key. A station that
     * publishes its delivery key (a public callsign registry, or any setup that
     * requires its traffic be decryptable with the published key) must not
     * ratchet.
     *
     * This can only take effect from creation: once an identity has announced a
     * ratchet, peers cache it for roughly 30 days, so switching it on for an
     * existing identity does nothing useful. Hence it is settable only at
     * creation time and is not editable afterwards.
     */
    val ratchetsDisabled: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LocalIdentityEntity
        return identityHash == other.identityHash
    }

    override fun hashCode(): Int = identityHash.hashCode()
}
