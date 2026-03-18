# language: pt

Funcionalidade: Gerenciamento de clientes

  # ==========================
  # CRIAR
  # ==========================

  Esquema do Cenario: Nao deve criar cliente quando campo obrigatorio estiver ausente
    Dado que nao existe cliente com cpf "<cpf>"
    Quando envio uma requisicao de cadastro com os dados:
      | nome     | <nome>     |
      | cpf      | <cpf>      |
      | telefone | <telefone> |
      | email    | <email>    |
      | endereco | <endereco> |
    Entao o status da resposta deve ser 400
    E o campo "message" da resposta deve conter "<mensagem>"

    Exemplos:
      | nome    | cpf         | telefone    | email       | endereco | mensagem               |
      |         | 12345678900 | 11999999999 | a@email.com | Rua A, 1 | Nome é obrigatório     |
      | Wallace |             | 11999999999 | a@email.com | Rua A, 1 | CPF é obrigatório      |
      | Wallace | 12345678900 |             | a@email.com | Rua A, 1 | Telefone é obrigatório |
      | Wallace | 12345678900 | 11999999999 |             | Rua A, 1 | Email é obrigatório    |
      | Wallace | 12345678900 | 11999999999 | a@email.com |          | Endereço é obrigatório |

  Cenario: Deve criar cliente quando todos os dados sao validos
    Dado que nao existe cliente com cpf "12345678900"
    Quando envio uma requisicao de cadastro com os dados:
      | nome     | Wallace           |
      | cpf      | 12345678900       |
      | telefone | 11999999999       |
      | email    | wallace@email.com |
      | endereco | Rua A, 123        |
    Entao o status da resposta deve ser 200
    E deve existir um cliente com cpf "12345678900"
    E o campo "cpf" da resposta deve ser "12345678900"
    E o campo "nome" da resposta deve ser "Wallace"

  Cenario: Nao deve criar cliente com CPF ja cadastrado
    Dado que ja existe um cliente cadastrado com cpf "12345678900"
    Quando envio uma requisicao de cadastro com os dados:
      | nome     | Outro           |
      | cpf      | 12345678900     |
      | telefone | 11988888888     |
      | email    | outro@email.com |
      | endereco | Rua B, 456      |
    Entao o status da resposta deve ser 409
    E o campo "message" da resposta deve conter "CPF já cadastrado"

  # ==========================
  # LISTAR
  # ==========================

  Cenario: Deve retornar lista vazia quando nenhum cliente estiver cadastrado
    Dado que nao existe nenhum cliente cadastrado
    Quando envio uma requisicao de listagem de clientes
    Entao o status da resposta deve ser 200
    E a resposta deve ser uma lista vazia

  Cenario: Deve retornar todos os clientes cadastrados
    Dado que os seguintes clientes estao cadastrados:
      | nome    | cpf         | telefone    | email         | endereco |
      | Wallace | 12345678900 | 11999999999 | w@email.com   | Rua A, 1 |
      | Ana     | 98765432100 | 11988888888 | ana@email.com | Rua B, 2 |
    Quando envio uma requisicao de listagem de clientes
    Entao o status da resposta deve ser 200
    E a resposta deve conter 2 clientes

  # ==========================
  # BUSCAR POR ID
  # ==========================

  Cenario: Deve retornar cliente quando ID existir
    Dado que ja existe um cliente cadastrado com cpf "12345678900"
    Quando envio uma requisicao de busca pelo id do cliente com cpf "12345678900"
    Entao o status da resposta deve ser 200
    E o campo "cpf" da resposta deve ser "12345678900"

  Cenario: Deve retornar erro quando buscar cliente por ID inexistente
    Quando envio uma requisicao de busca pelo id 999999
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"

  # ==========================
  # ATUALIZAR
  # ==========================

  Cenario: Deve atualizar cliente quando dados forem validos e CPF nao mudar
    Dado que ja existe um cliente cadastrado com cpf "12345678900"
    Quando envio uma requisicao de atualizacao do cliente com cpf "12345678900" com os dados:
      | nome     | Wallace Atualizado |
      | cpf      | 12345678900        |
      | telefone | 11977777777        |
      | email    | novo@email.com     |
      | endereco | Rua Nova, 999      |
    Entao o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Wallace Atualizado"
    E o campo "email" da resposta deve ser "novo@email.com"

  Cenario: Deve atualizar cliente quando novo CPF for unico
    Dado que ja existe um cliente cadastrado com cpf "12345678900"
    E que nao existe cliente com cpf "00000000001"
    Quando envio uma requisicao de atualizacao do cliente com cpf "12345678900" com os dados:
      | nome     | Wallace     |
      | cpf      | 00000000001 |
      | telefone | 11999999999 |
      | email    | w@email.com |
      | endereco | Rua A, 1    |
    Entao o status da resposta deve ser 200
    E o campo "cpf" da resposta deve ser "00000000001"

  Cenario: Nao deve atualizar cliente quando novo CPF ja pertencer a outro cliente
    Dado que ja existe um cliente cadastrado com cpf "12345678900"
    E que ja existe um cliente cadastrado com cpf "98765432100"
    Quando envio uma requisicao de atualizacao do cliente com cpf "12345678900" com os dados:
      | nome     | Wallace     |
      | cpf      | 98765432100 |
      | telefone | 11999999999 |
      | email    | w@email.com |
      | endereco | Rua A, 1    |
    Entao o status da resposta deve ser 409
    E o campo "message" da resposta deve conter "CPF já cadastrado"

  Cenario: Deve retornar erro ao atualizar cliente com ID inexistente
    Quando envio uma requisicao de atualizacao do id 999999 com os dados:
      | nome     | Fantasma    |
      | cpf      | 11111111111 |
      | telefone | 11999999999 |
      | email    | x@email.com |
      | endereco | Rua X, 0    |
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"

  # ==========================
  # DELETAR
  # ==========================

  Cenario: Deve deletar cliente quando ID existir
    Dado que ja existe um cliente cadastrado com cpf "12345678900"
    Quando envio uma requisicao de exclusao do cliente com cpf "12345678900"
    Entao o status da resposta deve ser 204
    E nao deve existir cliente com cpf "12345678900"

  Cenario: Deve retornar erro ao deletar cliente com ID inexistente
    Quando envio uma requisicao de exclusao do id 999999
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"
