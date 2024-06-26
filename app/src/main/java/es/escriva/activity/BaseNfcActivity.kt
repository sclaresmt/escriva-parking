package es.escriva.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.escriva.database.AppDatabase
import es.escriva.domain.Token
import es.escriva.repository.DayAndVehiclesRepository
import es.escriva.repository.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime

abstract class BaseNfcActivity : AppCompatActivity() {

    private val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
        try {
            addDataType("text/plain")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("Error al añadir tipo MIME", e)
        }
    }

    private val nfcTagDiscoverIntentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)

    private var nfcPendingIntent: PendingIntent? = null

    private lateinit var nfcAdapter: NfcAdapter

    private val nfcJob = Job()

    protected val nfcScope = CoroutineScope(Dispatchers.Main + nfcJob)

    private lateinit var tokenRepository: TokenRepository

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE // Requerido en Android 12 y superior
        )

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Initialize the database in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@BaseNfcActivity).also {
                tokenRepository = TokenRepository(it.tokenDao())
                dayAndVehiclesRepository =
                    DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(
            this,
            nfcPendingIntent,
            arrayOf(nfcIntentFilter, nfcTagDiscoverIntentFilter),
            null
        )
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val ndef = Ndef.get(tag)
        if (ndef == null) {
            Toast.makeText(this, "Por favor, mantenga el NFC cerca del dispositivo.",
                Toast.LENGTH_SHORT).show()
            finish()
        }
        ndef.connect()
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            onNewNfcTagDiscovered(ndef)
        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            readTokenAndOpenTokenActions(ndef)
        }
    }

    protected open fun onNewNfcTagDiscovered(ndef: Ndef) {
        nfcScope.launch {
            launch(Dispatchers.IO) {
                createNewTokenAndSetIdOnTag(ndef)
            }.join()
            readTokenAndOpenTokenActions(ndef)
        }
    }

    private fun readTokenAndOpenTokenActions(ndef: Ndef) {
        // Lanzar una nueva coroutine en el hilo de fondo
        nfcScope.launch(Dispatchers.IO) {
            val ndefMessage = ndef.ndefMessage
            if (ndefMessage == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@BaseNfcActivity, "Error al leer la etiqueta NFC. " +
                            "Intente de nuevo sin alejar la etiqueta.", Toast.LENGTH_LONG).show()
                }
            }
            val record = ndefMessage.records[0]
            val payload = String(record.payload)
            if (payload.contains("Admin", false)) {
                val nfcIntent = Intent(this@BaseNfcActivity, AdminActivity::class.java)
                startActivity(nfcIntent)
            } else {
                tokenRepository.upsert(Token(id = payload.toLong(), lastUpdatedDateTime = LocalDateTime.now()))
                val token = tokenRepository.findById(payload.toLong())
                val nfcIntent = Intent(this@BaseNfcActivity, TokenActivity::class.java)
                    .putExtra("token", token)
                startActivity(nfcIntent)
            }
        }
    }

    private suspend fun createNewTokenAndSetIdOnTag(ndef: Ndef) {
        val newToken = Token(lastUpdatedDateTime = LocalDateTime.now())
        val tokenId = tokenRepository.upsert(newToken)
        val newMessage =
            NdefMessage(arrayOf(NdefRecord.createMime("text/plain", tokenId.toString().toByteArray())))
        try {
            if (!ndef.isConnected) {
                throw IOException("La etiqueta NFC no está conectada.")
            }
            if (ndef.maxSize < newMessage.toByteArray().size) {
                throw IOException("La etiqueta NFC no tiene suficiente espacio para el mensaje.")
            }
            ndef.writeNdefMessage(newMessage)
            ndef.makeReadOnly()
        } catch (e: SecurityException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@BaseNfcActivity, "Error al escribir en la etiqueta " +
                        "NFC. Intente de nuevo sin alejar la etiqueta.", Toast.LENGTH_LONG).show()
            }
        }
    }

}