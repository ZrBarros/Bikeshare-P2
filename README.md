# Bikeshare | Programação 02
O objetivo do BIKESHARE é construir, como projeto da disciplina de Programação 02, a lógica de negócio completa de um sistema de bicicletas compartilhadas, acessada por uma Facade e validada por testes de aceitação automatizados sem qualquer camada de interface gráfica ou de persistência em banco de dados.

<p align="center">
  <img src="https://user-images.githubusercontent.com/91018438/204195385-acc6fcd4-05a7-4f25-87d1-cb7d5cc5c852.png" alt="animated" />
</p>

 ## Sobre o projeto

Assim como o WePayU tinha por propósito construir um sistema de folha de pagamento, o **BIKESHARE** tem por propósito construir um sistema de gerenciamento de um serviço de bicicletas compartilhadas (bike-sharing), no estilo dos sistemas usados em várias cidades brasileiras (ex.: Bike Itaú, Tembici).

O sistema mantém um cadastro de **usuários**, **estações** (pontos de retirada/devolução) e **bicicletas**. Um usuário retira uma bicicleta disponível em uma estação, usa-a por um período (um **aluguel**, também chamado de "corrida") e a devolve em qualquer estação com vaga livre. O sistema deve:

- Controlar a disponibilidade de bicicletas em cada estação (bicicletas presentes × vagas livres);
- Impedir a retirada de bicicletas indisponíveis (em uso, em manutenção) e de estações sem bicicletas;
- Impedir a devolução em estações sem vagas;
- Calcular o valor a cobrar por cada aluguel, de acordo com o **plano** do usuário (avulso ou assinante) e com a duração da corrida;
- Impedir que um usuário **inadimplente** (com pendências financeiras) faça uma nova retirada;
- Impedir que um usuário com um aluguel em andamento inicie um segundo aluguel simultâneo;
- Registrar o histórico de aluguéis de cada usuário e gerar relatórios (faturamento, bicicletas disponíveis/alugadas);
- Permitir desfazer/refazer operações (undo/redo);
- Rodar de forma **totalmente desacoplada de interface gráfica** — assim como no WePayU, só será implementada a lógica de negócio, acessada por uma **Façade** e validada por scripts de teste de aceitação (EasyAccept).

Está dentro do escopo: cadastro, consulta, alteração e remoção de usuários e estações; gestão de bicicletas convencionais e elétricas; retirada e devolução de bicicletas, com cálculo de tarifa; planos de tarifação avulso, assinante e planos customizados criados em tempo de execução; regras de bloqueio por inadimplência, estação cheia ou vazia e bicicleta indisponível; lançamento e conclusão de manutenção; relatórios de disponibilidade e faturamento; desfazer/refazer operações; e persistência do estado do sistema entre execuções.

Está fora do escopo: qualquer interface gráfica ou web, integração com um gateway de pagamento real, geolocalização	e qualquer forma de concorrência.		

## Glossário 

| Termo | Definição |
|---|---|
| **Usuário** | Pessoa cadastrada no sistema, identificada por um identificador único gerado automaticamente. Pode alugar bicicletas. |
| **Estação** | Ponto físico com um número fixo de vagas (`capacidade`), onde bicicletas ficam disponíveis para retirada e podem ser devolvidas. |
| **Bicicleta** | Veículo compartilhado, identificado unicamente, que pertence a uma estação enquanto não está em uso. |
| **Vaga** | Espaço disponível em uma estação para receber uma bicicleta. `vagas livres = capacidade − bicicletas presentes`. |
| **Aluguel (ou corrida)** | Período de uso de uma bicicleta por um usuário, do momento da retirada até a devolução. |
| **Plano** | Modalidade de cobrança contratada pelo usuário: *avulso* (paga por minuto/hora a cada corrida) ou *assinante* (paga mensalidade e tem franquia de minutos gratuitos). |
| **Tarifa** | Valor cobrado por um aluguel, calculado de acordo com o plano do usuário e a duração da corrida. |
| **Penalidade / taxa extra** | Cobrança adicional lançada sobre um usuário (ex.: atraso na devolução, dano à bicicleta, devolução fora da área de cobertura). Análoga à "taxa de serviço" do sindicato no WePayU. |
| **Inadimplência** | Situação de um usuário que possui pendências financeiras não quitadas; nesse estado, não pode iniciar novos aluguéis. |
| **Estado da bicicleta** | Condição operacional da bicicleta: disponível, em uso, em manutenção ou removida. |
| **Estado do aluguel** | Condição de uma corrida: em andamento, finalizada, cancelada ou expirada. |
| **Bicicleta convencional / elétrica** | Os dois tipos fixos de bicicleta suportados pelo sistema; a elétrica possui nível de bateria e cobrança adicional por minuto. |
| **Usuário pessoa física / jurídica** | Os dois tipos fixos de usuário; pessoa física se identifica por CPF, pessoa jurídica por CNPJ/razão social e pode ter fatura consolidada. |
| **Método de pagamento** | Forma pela qual o usuário paga suas cobranças: cartão, Pix ou saldo pré-carregado. |

---

## Implementação atual: US1–US9 (Milestone 1)

Este diretório parte dos arquivos do repositório original BikeShare: os scripts em `tests/`, a classe `Main`, as assinaturas da `Facade` e o EasyAccept. Foram implementados cadastro, consulta, alteração e remoção de usuários (pessoas físicas e jurídicas), planos avulso e assinante, métodos de pagamento e persistência do cadastro entre execuções. O cadastro de estações (US5) também está implementado, com capacidade inteira positiva, IDs sequenciais, consulta de vagas e persistência. A gestão de bicicletas convencionais e elétricas (US6) e as consultas de disponibilidade (US7) também estão implementadas, incluindo bateria, limite de vagas, remoção e persistência. Bicicletas elétricas abaixo da bateria mínima ocupam vaga, mas não contam como disponíveis. A retirada de bicicletas (US8) cria aluguéis, libera vagas, valida usuário/bateria/pagamento e impede remoção de usuários com corrida ativa. Aluguéis em andamento e seus IDs são persistidos. A devolução (US9) calcula o valor pelo plano vigente e pelo tipo de bicicleta, simula cobrança por cartão/Pix ou debita o saldo pré-carregado, e atualiza estação, bicicleta e usuário. A carência na mesma estação cancela sem cobrança; o tempo máximo excedido expira o aluguel, gera pendência e coloca a bicicleta em manutenção. Estação cheia ou saldo insuficiente recusam a devolução sem alterar o estado. As operações restantes de US10–US16 ficam para o Milestone 2.

O código de domínio está em `src/br/ufal/ic/p2/bikeshare/model/`; a lógica de cadastro, em `service/`; a gravação e leitura do estado, em `persistence/`. A configuração `plano.assinante.mensalidadePadrao` fica em `config.properties` (valor inicial: 30,00). O estado é salvo em `bikeshare-state.bin` ao chamar `encerrarSistema` e restaurado quando uma nova `Facade` é criada. `zerarSistema` reinicia o estado em memória; a gravação ocorre ao encerrar.

Requer JDK 11 ou superior. Na raiz do projeto, compile e execute os testes do Milestone 1:

```sh
javac --release 11 -cp lib/easyaccept.jar -d out $(find src -name '*.java')
java -cp out:lib/easyaccept.jar Main --m1
```

Os scripts com sufixo `_1` verificam a persistência e são executados depois do respectivo script sem sufixo. O modo `--m1` roda apenas US1–US9. Sem argumentos, `Main` mantém a execução integral original (US1–US16), incluindo funcionalidades pendentes do M2. Para executar um script isolado, use `java -cp out:lib/easyaccept.jar Main tests/us1.txt`.


Validação de US1–US9: 834 verificações aprovadas nos dezesseis scripts acima. O limite `bicicleta.eletrica.bateriaMinima` é configurável em `config.properties` (padrão: 15%).


### Configuração de cobrança

Os parâmetros ficam em `config.properties` e são carregados na criação da `Facade`:

| Propriedade | Padrão |
|---|---:|
| `tarifa.avulso.minuto` | 0.50 |
| `tarifa.assinante.franquiaMinutos` | 60 |
| `tarifa.assinante.minutoExcedente` | 0.30 |
| `tarifa.eletrica.adicionalMinuto` | 0.20 |
| `aluguel.carenciaMinutos` | 2 |
| `aluguel.tempoMaximoMinutos` | 1440 |
| `aluguel.penalidadeAtraso` | 10.00 |

O adicional da elétrica incide sobre toda a duração da corrida normal, mesmo dentro da franquia do assinante. Cancelamento e expiração não cobram tarifa nem adicional. Exatamente 1440 minutos ainda é uma corrida normal; acima disso, expira. A mensalidade não é debitada ao devolver. Valores monetários são calculados com `BigDecimal` e arredondados para centavos na cobrança.

A validação foi executada em cópia temporária, com cada script em um processo separado. Para executar manualmente na pasta original, lembre que os scripts usam `zerarSistema` e `encerrarSistema`, substituindo o arquivo de estado local. Nenhum script fornecido foi alterado. Também foram verificados limites personalizados e a leitura de corridas em andamento salvas pela implementação anterior à US9.


### Verificações do domínio

Os testes adicionais em `tests/java/` verificam construtores e alterações inválidas, saldo preservado após recusa, transições de estado, coleções protegidas e falhas de persistência. Não substituem nem modificam os scripts fornecidos.

```sh
javac --release 11 -cp out:lib/easyaccept.jar -d out $(find tests/java -name '*.java')
java -cp out:lib/easyaccept.jar br.ufal.ic.p2.bikeshare.service.VerificacaoDominio
```

São 64 verificações adicionais. Os testes de aceitação também foram executados em processos separados, totalizando 834 verificações aprovadas. A compilação com `--release 11` e a execução foram verificadas com JDK 26.

### Organização

`EstadoSistema` guarda os cadastros e contadores em campos privados. Os serviços acessam operações de cadastro/busca e cópias não modificáveis das coleções. Construtores e métodos do domínio validam seus próprios dados antes de alterar o estado.

Bicicletas e aluguéis usam enums para os estados. A serialização mantém o formato anterior, de modo que os arquivos já salvos continuam legíveis. As exceções derivam de `BikeShareException`, distinguindo validação, entidade não encontrada, operação não permitida, configuração e persistência. Falhas técnicas preservam a causa.
