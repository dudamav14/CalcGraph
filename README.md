# CalcGraph - Calculadora Gráfica e Estatística

O **CalcGraph** é uma aplicação Java robusta, baseada em **JavaFX**, que combina as funcionalidades de uma calculadora científica, um plotador de gráficos 2D e uma ferramenta de análise estatística. O sistema utiliza algoritmos clássicos de parsing para processar expressões matemáticas complexas e oferece persistência de dados para histórico e favoritos.

---

## Funcionalidades Principais

* **Modo Cálculo (Científico):** Avaliação de expressões aritméticas complexas com suporte a precedência de operadores e funções trigonométricas/logarítmicas.
* **Modo Gráfico:**
    * Plotagem de funções matemáticas em tempo real (ex: $f(x) = x^2$).
    * Suporte a **zoom dinâmico** e **arrastamento (panning)** do plano cartesiano.
    * Identificação automática de raízes para expressões quadráticas.
    * Comparação entre diferentes funções.
* **Modo Numérico (Binário):** Realização de cálculos e operações diretamente em base binária.
* **Estatística Avançada:** Funções integradas para cálculo de média, moda, mediana, variância, desvio padrão e testes de hipótese (Z e T).
* **Persistência de Dados:**
    * **Histórico:** Registro automático de todas as expressões avaliadas para consulta posterior.
    * **Favoritos:** Capacidade de salvar funções com apelidos e descrições personalizadas via JPA.

---

## Tecnologias Utilizadas

* **Linguagem:** Java 8+.
* **Interface Gráfica:** JavaFX (FXML).
* **Persistência (ORM):** JPA 2.2 com EclipseLink.
* **Base de Dados:** MySQL.
* **Build System:** Ant / NetBeans.
* **Testes:** JUnit 4 para validação de lógica de parsing e avaliação.

---

## Arquitetura do Sistema

O projeto segue uma estrutura modular dividida em:

1.  **Model:** Contém a lógica de negócio, incluindo:
    * `AnalisadorDeExpressoes`: Orquestrador da validação e avaliação.
    * `ExpressionParser`: Tokenizador baseado em Regex.
    * `ShuntingYardAlgorithm`: Implementação do algoritmo de Dijkstra para conversão de infixo para RPN (Notação Polonesa Inversa).
    * `PostfixEvaluator`: Avaliador de pilhas para calcular o resultado final.
2.  **View & Controller:** Gestão da interface e eventos do usuário através do `CalculatorController`.
3.  **Persistence:** Repositórios e utilitários para gestão das entidades `Expressao` e `Favorito`.

---

## Pré-requisitos

* **Java JDK 8** ou superior.
* Servidor **MySQL** rodando na porta 3306.
* Base de dados chamada `calcgraph`.
* **Usuário:** `calcgraph` / **Senha:** `calcgraph` (configurável em `src/META-INF/persistence.xml`).

---

## Instalação e Execução

1.  Clone o repositório.
2.  Configure a base de dados MySQL conforme o arquivo `persistence.xml`.
3.  Compile o projeto usando o NetBeans ou via Ant:
   
    ```bash
    ant compile
    ```
5.  Execute a aplicação:
   
    ```bash
    ant run
    ```

---

## Testes

O projeto possui uma suite de testes unitários abrangente:
* **Lógica de Operadores:** `OperatorPrecedenceTestes`.
* **Classificação Quadrática:** `QuadraticExpressionClassifierTestes`.
* **Integração de Avaliação:** `AnalisadorDeExpressoesTestes`.

Para rodar os testes:
```bash
ant test
```

## Autores
* **Caio (Product Owner e Desenvolvedor)** - Desenvolvimento Inicial e Lógica de Parsing.
* **Duda (Scrum Master e Desenvolvedora)** - Revisão e Lógica de Integração
* **Luís Gustavo (Desenvolvedor)** - Testes Unitários
* **Leonardo (Desenvolvedor)** - Front-End
* **Otávio (Desenvolvedor)** - Banco de Dados
* **Rogério (Desenvolvedor)** - Lógica Matemática
