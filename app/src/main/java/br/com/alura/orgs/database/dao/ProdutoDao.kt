package br.com.alura.orgs.database.dao

import androidx.room.Insert
import androidx.room.Query
import br.com.alura.orgs.model.Produto

interface ProdutoDao {

    @Query("SELECT * FROM Produto")
    fun buscarTodos()

    @Insert
    fun salva(vararg produto: Produto)
}