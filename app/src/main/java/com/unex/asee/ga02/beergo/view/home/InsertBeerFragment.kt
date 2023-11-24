package com.unex.asee.ga02.beergo.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.unex.asee.ga02.beergo.database.BeerGoDatabase
import com.unex.asee.ga02.beergo.databinding.FragmentInsertBeerBinding
import com.unex.asee.ga02.beergo.model.Beer
import com.unex.asee.ga02.beergo.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragmento para la inserción de nuevas cervezas.
 */
class InsertBeerFragment : Fragment() {

    // View Binding
    private var _binding: FragmentInsertBeerBinding? = null
    private val binding get() = _binding!!

    // Database
    private lateinit var db: BeerGoDatabase

    // ViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: User


    /**
     * Método llamado cuando se crea la instancia del fragmento.
     *
     * @param savedInstanceState Bundle que contiene el estado previamente guardado del fragmento.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtener instancia de la base de datos
        db = BeerGoDatabase.getInstance(requireContext())!!
        // Obtener el ViewModel
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        currentUser = userViewModel.getUser()
    }

    /**
     * Método llamado para crear y devolver la vista asociada con el fragmento.
     *
     * @param inflater El objeto LayoutInflater que puede ser utilizado para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, este es el grupo de vistas al que se adjuntará el fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido a partir de un estado guardado.
     * @return La vista raíz del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInsertBeerBinding.inflate(inflater, container, false)

        setupInsertButton()

        return binding.root
    }

    /**
     * Configura el botón de inserción para manejar la lógica de inserción de cervezas.
     */
    private fun setupInsertButton() {
        binding.buttonInsertBeer.setOnClickListener {
            val title = binding.editTextBeerName.text.toString()
            val description = binding.editTextBeerDescription.text.toString()
            val year = binding.editTextYear.text.toString()
            val abvString = binding.editTextAlcoholPercentage.text.toString()

            if (areFieldsValid(title, description, year, abvString)) {
                val abv = abvString.toDoubleOrNull()

                if (abv != null) {
                    val beer = createBeer(title, description, year, abv)
                    lifecycleScope.launch(Dispatchers.IO) {
                        insertarCerveza(beer)
                        showNotification("Cerveza insertada")
                    }
                } else {
                    // Manejar el caso donde el campo de porcentaje de alcohol no es un número válido
                    handleInvalidAbv()
                }
            } else {
                // Manejar el caso donde los campos no están completos
                handleIncompleteFields()
            }
        }
    }

    /**
     * Verifica si los campos necesarios para la inserción son válidos.
     *
     * @param title Nombre de la cerveza.
     * @param description Descripción de la cerveza.
     * @param year Año de la cerveza.
     * @param abvString Porcentaje de alcohol en formato de cadena.
     * @return `true` si los campos son válidos, `false` de lo contrario.
     */
    private fun areFieldsValid(
        title: String,
        description: String,
        year: String,
        abvString: String
    ): Boolean {
        return title.isNotEmpty() && description.isNotEmpty() && year.isNotEmpty() && abvString.isNotEmpty()
    }

    /**
     * Maneja el caso donde el campo de porcentaje de alcohol no es un número válido.
     */
    private fun handleInvalidAbv() {
        // Manejar el caso donde el campo de porcentaje de alcohol no es un número válido
        showNotification("Porcentaje de alcohol no válido. Introduce un número válido.")
    }

    /**
     * Maneja el caso donde los campos no están completos.
     */
    private fun handleIncompleteFields() {
        // Manejar el caso donde los campos no están completos
        showNotification("Completa todos los campos antes de insertar la cerveza.")
    }

    /**
     * Crea una instancia de la clase [Beer] con los datos proporcionados.
     *
     * @param title Nombre de la cerveza.
     * @param description Descripción de la cerveza.
     * @param year Año de la cerveza.
     * @param abv Porcentaje de alcohol.
     * @param image URL de la imagen de la cerveza.
     * @param insertedBy ID del usuario que insertó la cerveza.
     * @return Una instancia de la clase [Beer].
     */
    private fun createBeer(
        title: String,
        description: String,
        year: String,
        abv: Double
    ): Beer {
        return Beer(
            beerId = 0,
            title = title,
            description = description,
            year = year,
            abv = abv,
            image = "url_imagen", //TODO: no se que poner
            insertedBy = currentUser.userId
        )
    }

    /**
     * Inserta una nueva cerveza en la base de datos de BeerGo de manera asíncrona.
     *
     * @param beer La cerveza que se va a insertar en la base de datos.
     */
    private suspend fun insertarCerveza(beer: Beer) {
        db.beerDao().insert(beer)
    }

    /**
     * Método llamado cuando la vista y cualquier fragmento asociado con la vista son eliminados.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Evitar fugas de memoria
    }

    private fun showNotification(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}