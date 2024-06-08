package es.escriva

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.escriva.domain.Token
import es.escriva.repository.TokenRepository
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
        try {
            addDataType("*/*") // Para manejar todos los tipos MIME
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("Error al añadir tipo MIME", e)
        }
    }

    private val nfcPendingIntent = PendingIntent.getActivity(
        this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
        PendingIntent.FLAG_MUTABLE // Requerido en Android 12 y superior
    )

    private lateinit var nfcAdapter: NfcAdapter

    private lateinit var tokenRepository: TokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        AppDatabase.getDatabase(this).also {
            tokenRepository = TokenRepository(it.tokenDao())
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, arrayOf(nfcIntentFilter), null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndef = Ndef.get(tag)
            val ndefMessage = ndef.cachedNdefMessage
            val record = ndefMessage.records[0]
            val payload = String(record.payload)

            if (payload.isEmpty()) {
                val token = Token(dateTimeCreation = LocalDateTime.now())
                val tokenId = tokenRepository.insert(token) // Necesitas una referencia a tu TokenDao para hacer esto

                val newRecord = NdefRecord.createMime("text/plain", tokenId.toString().toByteArray())
                val newMessage = NdefMessage(arrayOf(newRecord))
                ndef.writeNdefMessage(newMessage)

                ndef.makeReadOnly()
            }

            startActivity(Intent(this, NfcAction::class.java))
        }
    }

}