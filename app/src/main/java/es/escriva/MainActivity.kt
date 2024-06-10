package es.escriva

import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import es.escriva.activity.TokenActivity
import es.escriva.activity.VehicleRecordActivity
import es.escriva.database.AppDatabase
import es.escriva.domain.Token
import es.escriva.repository.DayAndVehiclesRepository
import es.escriva.repository.TokenRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private val nfcIntentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
        try {
            addDataType("text/plain")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("Error al añadir tipo MIME", e)
        }
    }

    private val nfcTagDiscoverIntentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)

    private var nfcPendingIntent: PendingIntent? = null

    private val nfcJob = Job()

    private val nfcScope = CoroutineScope(Dispatchers.Main + nfcJob)

    private var progressDialog: ProgressDialog? = null

    private lateinit var nfcAdapter: NfcAdapter

    private lateinit var tokenRepository: TokenRepository

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nfcPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE // Requerido en Android 12 y superior
        )

        progressDialog = ProgressDialog(this).apply {
            setMessage("Leyendo NFC...")
            setCancelable(false)
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Initialize the database in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@MainActivity).also {
                tokenRepository = TokenRepository(it.tokenDao())
                dayAndVehiclesRepository = DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
            }
        }

        val showRecordsButton: Button = findViewById(R.id.btn_show_records)
        showRecordsButton.setOnClickListener {
            showVehicleRecordsForActiveDay()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, arrayOf(nfcIntentFilter, nfcTagDiscoverIntentFilter), null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
        progressDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        nfcJob.cancel()
        progressDialog?.dismiss()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            // Formatear el tag con mensaje NDEF vacío
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndef = Ndef.get(tag)
            val newMessage = NdefMessage(arrayOf(NdefRecord.createMime("text/plain", "".toByteArray())))
            ndef.connect()
            ndef.writeNdefMessage(newMessage)
            ndef.close()
            readTokenAndOpenTokenActions(intent)
        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            readTokenAndOpenTokenActions(intent)
        }
    }

    private fun readTokenAndOpenTokenActions(intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val ndef = Ndef.get(tag)
        val activityContext = this
        progressDialog?.show()

        // Lanzar una nueva coroutine en el hilo de fondo
        nfcScope.launch(Dispatchers.IO) {
            val ndefMessage = ndef.cachedNdefMessage
            val record = ndefMessage.records[0]
            val payload = String(record.payload)
            val tokenId = obtainTokenIdAndMakeTokenReadOnly(payload, ndef)

            val token = tokenRepository.findById(tokenId)
            val nfcIntent = Intent(activityContext, TokenActivity::class.java)
                .putExtra("token", token)
            startActivity(nfcIntent)
        }
    }

    private fun obtainTokenIdAndMakeTokenReadOnly(
        payload: String,
        ndef: Ndef
    ): Long {
        if (payload.isEmpty()) {
            val newToken = Token(lastUpdatedDateTime = LocalDateTime.now())
            val tokenId = tokenRepository.upsert(newToken)

            val newRecord =
                NdefRecord.createMime("text/plain", tokenId.toString().toByteArray())
            val newMessage = NdefMessage(arrayOf(newRecord))
            ndef.connect()
            ndef.writeNdefMessage(newMessage)
            ndef.makeReadOnly()
            ndef.close()
            return tokenId
        }
        return payload.toLong()
    }

    fun showVehicleRecordsForActiveDay() {
        val activeDay = dayAndVehiclesRepository.getActiveDay()
        val intent = Intent(this, VehicleRecordActivity::class.java).apply {
            putExtra("day", activeDay)
        }
        startActivity(intent)
    }

}