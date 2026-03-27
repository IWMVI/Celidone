# language: pt

Funcionalidade: Gerenciamento de clientes

  # ==========================
  # CRIAR
  # ==========================

  Esquema do Cenario: Nao deve criar cliente quando campo obrigatorio estiver ausente
    Dado que nao existe cliente com cpf "<cpfCnpj>"
    Quando envio uma requisicao de cadastro com os dados:
      | nome   | cpfCnpj     | email         | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | <nome> | <cpfCnpj>   | <email>       | <celular>    | <cep>    | <logradouro>  | <numero> | <cidade> | <bairro> | <estado> | <complemento> | MASCULINO |
    Entao o status da resposta deve ser 400
    E o campo "message" da resposta deve conter "<mensagem>"

    Exemplos:
      | nome    | cpfCnpj     | email         | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | mensagem                         |
      |         | 12345678901 | a@email.com   | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Nome é obrigatório               |
      | Teste   |             | a@email.com   | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | CPF ou CNPJ é obrigatório        |
      | Teste   | 12345678901 |               | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Email é obrigatório              |
      | Teste   | 12345678901 | a@email.com   |              | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Celular é obrigatório           |

  Esquema do Cenario: Nao deve criar cliente quando campo contiver apenas espacos
    Dado que nao existe cliente com cpf "<cpfCnpj>"
    Quando envio uma requisicao de cadastro com os dados:
      | nome   | cpfCnpj     | email         | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | <nome> | <cpfCnpj>   | <email>       | <celular>    | <cep>    | <logradouro>  | <numero> | <cidade> | <bairro> | <estado> | <complemento> | MASCULINO |
    Entao o status da resposta deve ser 400
    E o campo "message" da resposta deve conter "<mensagem>"

    Exemplos:
      | nome    | cpfCnpj     | email         | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | mensagem                         |
      |         | 12345678901 | a@email.com   | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Nome é obrigatório               |
      |   | 12345678901 | a@email.com   | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Nome é obrigatório               |
      | Teste   |     | a@email.com   | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | CPF ou CNPJ é obrigatório        |
      | Teste   | 12345678901 |     | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Email é obrigatório              |
      | Teste   | 12345678901 | a@email.com   |     | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | Celular é obrigatório           |

  Cenario: Nao deve criar cliente quando endereco for nulo
    Dado que nao existe cliente com cpf "12345678901"
    Quando envio uma requisicao de cadastro com os dados:
      | nome   | cpfCnpj     | email         | celular      | cep | logradouro | numero | cidade | bairro | estado | complemento | sexo        |
      | Teste  | 12345678901 | a@email.com  | 11999999999  |     |            |        |        |        |        |              | MASCULINO  |
    Entao o status da resposta deve ser 400
    E o campo "message" da resposta deve conter "Endereço é obrigatório"

  Cenario: Deve criar cliente quando todos os dados sao validos
    Dado que nao existe cliente com cpf "12345678901"
    Quando envio uma requisicao de cadastro com os dados:
      | nome           | cpfCnpj     | email           | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva  | 12345678901 | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
    Entao o status da resposta deve ser 200
    E deve existir um cliente com cpf "12345678901"
    E o campo "cpfCnpj" da resposta deve ser "12345678901"
    E o campo "nome" da resposta deve ser "Joao da Silva"

  Cenario: Nao deve criar cliente com CPF ja cadastrado
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de cadastro com os dados:
      | nome              | cpfCnpj     | email             | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Empresa XPTO LTDA | 12345678901 | empresa@email.com | 11988888888  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
    Entao o status da resposta deve ser 409
    E o campo "message" da resposta deve conter "CPF ou CNPJ já cadastrado"

  # ==========================
  # LISTAR / BUSCAR COM FILTRO
  # ==========================

  Cenario: Deve retornar lista vazia quando nenhum cliente estiver cadastrado
    Dado que nao existe nenhum cliente cadastrado
    Quando envoy requisicao de listagem de clientes
    Entao o status da resposta deve ser 200
    E a resposta deve ser uma lista vazia

  Cenario: Deve retornar todos os clientes cadastrados
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
      | Empresa XPTO  | 12345678000195| empresa@email.com| 11988888888  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | FEMININO    |
    Quando envoy requisicao de listagem de clientes
    Entao o status da resposta deve ser 200
    E a resposta deve conter 2 clientes

  Cenario: Deve retornar todos os clientes quando busca for nula
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
    Quando envoy requisicao de listagem de clientes sem filtro
    Entao o status da resposta deve ser 200
    E a resposta deve conter 1 clientes

  Cenario: Deve retornar todos os clientes quando busca for vazia
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
    Quando envoy requisicao de listagem de clientes com filtro ""
    Entao o status da resposta deve ser 200
    E a resposta deve conter 1 clientes

  Cenario: Deve buscar clientes por termo quando filtro for valido
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
      | Maria Santos  | 98765432100   | maria@email.com | 11988888888  | 01001000 | Rua Teste     | 200    | Sao Paulo  | Vila   | SP     | Sala 200    | FEMININO    |
    Quando envoy requisicao de listagem de clientes com filtro "Joao"
    Entao o status da resposta deve ser 200
    E a resposta deve conter 1 clientes

  Cenario: Deve buscar clientes por CPF quando filtro for valido
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
      | Maria Santos  | 98765432100   | maria@email.com | 11988888888  | 01001000 | Rua Teste     | 200    | Sao Paulo  | Vila   | SP     | Sala 200    | FEMININO    |
    Quando envoy requisicao de listagem de clientes com filtro "12345678901"
    Entao o status da resposta deve ser 200
    E a resposta deve conter 1 clientes

  Cenario: Deve buscar clientes por email quando filtro for valido
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
      | Maria Santos  | 98765432100   | maria@email.com | 11988888888  | 01001000 | Rua Teste     | 200    | Sao Paulo  | Vila   | SP     | Sala 200    | FEMININO    |
    Quando envoy requisicao de listagem de clientes com filtro "maria"
    Entao o status da resposta deve ser 200
    E a resposta deve conter 1 clientes

  Cenario: Deve remover espacos em branco do termo de busca
    Dado que os seguintes clientes estao cadastrados:
      | nome          | cpfCnpj       | email            | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Joao da Silva | 12345678901   | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
    Quando envoy requisicao de listagem de clientes com filtro "   Joao   "
    Entao o status da resposta deve ser 200
    E a resposta deve conter 1 clientes

  # ==========================
  # BUSCAR POR ID
  # ==========================

  Cenario: Deve retornar cliente quando ID existir
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de busca pelo id do cliente com cpf "12345678901"
    Entao o status da resposta deve ser 200
    E o campo "cpfCnpj" da resposta deve ser "12345678901"

  Cenario: Deve retornar erro quando buscar cliente por ID inexistente
    Quando envio uma requisicao de busca pelo id 999999
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"

  # ==========================
  # ATUALIZAR
  # ==========================

  Cenario: Deve atualizar cliente quando dados forem validos e CPF nao mudar
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envoy requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome                | cpfCnpj     | email                      | celular      | cep      | logradouro           | numero | cidade          | bairro | estado | complemento | sexo        |
      | Cliente Atualizado  | 12345678901 | cliente.atualizado@email.com| 11977777777  | 20040002 | Rua da Assembleia   | 200    | Rio de Janeiro  | Centro | RJ     | Apto 502    | MASCULINO   |
    Entao o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Cliente Atualizado"
    E o campo "email" da resposta deve ser "cliente.atualizado@email.com"

  Cenario: Deve atualizar cliente quando novo CPF for unico
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    E que nao existe cliente com cpf "00000000001"
    Quando envoy requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome    | cpfCnpj     | email        | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Teste   | 00000000001 | w@email.com | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | FEMININO    |
    Entao o status da resposta deve ser 200
    E o campo "cpfCnpj" da resposta deve ser "00000000001"

  Cenario: Nao deve atualizar cliente quando novo CPF ja pertencer a outro cliente
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    E que ja existe um cliente cadastrado com cpf "12345678000195"
    Quando envoy requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome    | cpfCnpj       | email              | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Teste   | 12345678000195| 12345678000195@email.com | 11999999999  | 01001000 | Rua Exemplo   | 100    | Sao Paulo  | Centro | SP     | Sala 101    | MASCULINO   |
    Entao o status da resposta deve ser 409
    E o campo "message" da resposta deve conter "CPF ou CNPJ já cadastrado"

  Cenario: Deve retornar erro ao atualizar cliente com ID inexistente
    Quando envoy requisicao de atualizacao do id 999999 com os dados:
      | nome    | cpfCnpj     | email        | celular      | cep      | logradouro | numero | cidade    | bairro    | estado | complemento | sexo        |
      | Fantasma| 11111111111 | x@email.com | 11999999999  | 00000000 | Rua X      | 1      | Cidade X  | Bairro X  | SP     | Apto 1      | MASCULINO   |
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"

  Cenario: Deve permitir atualizar quando mesmo CPF for mantido
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envoy requisicao de atualizacao do cliente com cpf "12345678901" com os dados:
      | nome              | cpfCnpj     | email                  | celular      | cep      | logradouro    | numero | cidade     | bairro | estado | complemento | sexo        |
      | Nome Atualizado   | 12345678901 | novo.email@email.com  | 11977777777  | 01001000 | Rua Nova      | 50     | Sao Paulo  | Bela   | SP     | Apto 50     | FEMININO    |
    Entao o status da resposta deve ser 200
    E o campo "nome" da resposta deve ser "Nome Atualizado"
    E o campo "email" da resposta deve ser "novo.email@email.com"

  # ==========================
  # DELETAR
  # ==========================

  Cenario: Deve deletar cliente quando ID existir
    Dado que ja existe um cliente cadastrado com cpf "12345678901"
    Quando envio uma requisicao de exclusao do cliente com cpf "12345678901"
    Entao o status da resposta deve ser 204
    E nao deve existir cliente com cpf "12345678901"

  Cenario: Deve retornar erro ao deletar cliente com ID inexistente
    Quando envio uma requisicao de exclusao do id 999999
    Entao o status da resposta deve ser 404
    E o campo "message" da resposta deve conter "Cliente não encontrado"