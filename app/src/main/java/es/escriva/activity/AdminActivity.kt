package es.escriva.activity

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import es.escriva.R
import es.escriva.database.AppDatabase
import es.escriva.repository.DayAndVehiclesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

class AdminActivity : BaseNfcActivity() {

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    private var adminDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@AdminActivity).also {
                dayAndVehiclesRepository =
                    DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
            }
        }

        val showRecordsButton: Button = findViewById(R.id.btn_show_records)
        showRecordsButton.setOnClickListener {
            showVehicleRecordsForActiveDay()
        }

        val createAdminButton: Button = findViewById(R.id.btn_create_admin)
        createAdminButton.setOnClickListener {
            showDialogAndWaitForToken()
        }

        // Aquí es donde verificas tu condición
        lifecycleScope.launch {
            showAlertForOldActiveDay()
        }
    }

    override fun onNewNfcTagDiscovered(ndef: Ndef) {
        if (adminDialog?.isShowing == true) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val newMessage =
                        NdefMessage(arrayOf(NdefRecord.createMime("text/plain", "Admin".toByteArray())))
                    ndef.writeNdefMessage(newMessage)
                    ndef.makeReadOnly()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AdminActivity, "Token registrado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AdminActivity, "Error al registrar el token", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            adminDialog!!.dismiss()
        } else {
            super.onNewNfcTagDiscovered(ndef)
        }
    }

    private fun showDialogAndWaitForToken() {
        adminDialog = AlertDialog.Builder(this@AdminActivity)
            .setTitle("Esperando token sin formato...")
            .setMessage("Por favor, acerque el token sin formato al dispositivo.")
            .setCancelable(true)
            .create()
        adminDialog!!.show()
    }

    private suspend fun showAlertForOldActiveDay() {
        val activeDay = dayAndVehiclesRepository.getActiveDay()
        if (activeDay?.date?.isBefore(LocalDate.now()) == true) {
            // Crear el AlertDialog
            val builder = AlertDialog.Builder(this@AdminActivity)
            builder.setTitle("El día activo es anterior al día de hoy. ¿Desea cerrar el día?")
            builder.setMessage(
                "Esto anulará los registros de vehículos del día activo que no tengan " +
                        "salida registrada y permitirá registrar los nuevos vehículos en el día " +
                        "actual."
            )

            // Añadir botones
            builder.setPositiveButton("Aceptar") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    dayAndVehiclesRepository.closeDay(activeDay)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AdminActivity, "Día cerrado correctamente", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }

            // Mostrar el diálogo
            builder.show()
        }
    }

    private fun showVehicleRecordsForActiveDay() {
        CoroutineScope(Dispatchers.IO).launch {
            var activeDay = dayAndVehiclesRepository.getActiveDay()
            if (activeDay == null) {
                activeDay = dayAndVehiclesRepository.newActiveDay()
            }
            withContext(Dispatchers.Main) {
                val intent = Intent(this@AdminActivity, VehicleRecordActivity::class.java).apply {
                    putExtra("day", activeDay)
                }
                startActivity(intent)
            }
        }
    }

}