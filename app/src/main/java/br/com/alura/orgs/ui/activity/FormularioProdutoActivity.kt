package br.com.alura.orgs.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.database.dao.ProdutoDao
import br.com.alura.orgs.databinding.ActivityFormularioProdutoBinding
import br.com.alura.orgs.extensions.tentaCarregarImagem
import br.com.alura.orgs.model.Produto
import br.com.alura.orgs.ui.dialog.FormularioImagemDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFormularioProdutoBinding.inflate(layoutInflater)
    }
    private var url: String? = null
    private var produtoId: Long = 0L
    private val produtoDao: ProdutoDao by lazy { AppDatabase.instancies(this).produtoDao() }
    private val scope = CoroutineScope(IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = "Cadastrar produto"
        configuraBotaoSalvar()
        binding.activityFormularioProdutoImagem.setOnClickListener {
            FormularioImagemDialog(this)
                .mostra(url) { imagem ->
                    url = imagem
                    binding.activityFormularioProdutoImagem.tentaCarregarImagem(url)
                }
        }
        produtoId =  intent.getLongExtra(CHAVE_PRODUTO_ID, 0L)
    }

    override fun onResume() {
        super.onResume()
        tentaBuscarPorId()
    }

    private fun tentaBuscarPorId() {
        scope.launch {
            produtoDao.buscaPorId(produtoId)?.let {
                withContext(Dispatchers.Main) {
                    preencheFormulario(it)
                }
            }
        }
    }

    private fun preencheFormulario(produto: Produto) {
        title = "Editar Produto"
        url = produto.imagem
        produtoId = produto.id
        binding.activityFormularioProdutoImagem.tentaCarregarImagem(produto.imagem)
        binding.activityFormularioProdutoNome.setText(produto.nome)
        binding.activityFormularioProdutoDescricao.setText(produto.descricao)
        binding.activityFormularioProdutoValor.setText(produto.valor.toString())
    }

    private fun configuraBotaoSalvar() {
        val botaoSalvar = binding.activityFormularioProdutoBotaoSalvar
        val db = AppDatabase.instancies(this)
        val produtoDao = db.produtoDao()
        botaoSalvar.setOnClickListener {
            scope.launch {
                val produtoNovo = criaProduto()
                produtoDao.salva(produtoNovo)
                finish()
            }
        }
    }

    private fun criaProduto(): Produto {
        val campoNome = binding.activityFormularioProdutoNome
        val nome = campoNome.text.toString()
        val campoDescricao = binding.activityFormularioProdutoDescricao
        val descricao = campoDescricao.text.toString()
        val campoValor = binding.activityFormularioProdutoValor
        val valorEmTexto = campoValor.text.toString()
        val valor = if (valorEmTexto.isBlank()) {
            BigDecimal.ZERO
        } else {
            BigDecimal(valorEmTexto)
        }

        return Produto(
            id = produtoId,
            nome = nome,
            descricao = descricao,
            valor = valor,
            imagem = url
        )
    }

}