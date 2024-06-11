package es.escriva.activity

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import es.escriva.R
import es.escriva.database.AppDatabase
import es.escriva.databinding.ActivityTokenActionBinding
import es.escriva.domain.Token
import es.escriva.repository.DayAndVehiclesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TokenActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityTokenActionBinding

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTokenActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@TokenActivity).also {
                dayAndVehiclesRepository =
                    DayAndVehiclesRepository(it.dayDao(), it.vehicleRecordDao())
            }
        }

//        setSupportActionBar(binding.toolbar)

//        val navController = findNavController(R.id.nav_host_fragment_content_nfc_action)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//        }

        // Recuperar el objeto Token del Intent
        val token = intent.getSerializableExtra("token") as Token

        // Obtén el botón de la vista
        val enterButton = findViewById<Button>(R.id.enter_button)
        // Agrega un OnClickListener al botón con una coroutine
        enterButton.setOnClickListener {
            enterAction(token)
        }

        val exitButton = findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            exitAction(token)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_nfc_action)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun enterAction(token: Token) {
        lifecycleScope.launch(Dispatchers.IO) {
            dayAndVehiclesRepository.enterAction(token)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TokenActivity, "Entrada registrada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun exitAction(token: Token) {
        lifecycleScope.launch(Dispatchers.IO) {
            dayAndVehiclesRepository.exitAction(token)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@TokenActivity, "Salida registrada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}