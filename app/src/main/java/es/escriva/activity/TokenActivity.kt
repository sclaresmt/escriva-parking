package es.escriva.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import es.escriva.R
import es.escriva.databinding.ActivityTokenActionBinding
import es.escriva.domain.Token
import es.escriva.repository.DayAndVehiclesRepository
import kotlinx.coroutines.launch

class TokenActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityTokenActionBinding

    private lateinit var dayAndVehiclesRepository: DayAndVehiclesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTokenActionBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_nfc_action)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

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
            lifecycleScope.launch {
                enterAction(token)
            }
        }

        val exitButton = findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            lifecycleScope.launch {
                exitAction(token)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_nfc_action)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun enterAction(token: Token) {
        this.dayAndVehiclesRepository.enterAction(token)
    }

    private fun exitAction(token: Token) {
        this.dayAndVehiclesRepository.exitAction(token)
    }

}