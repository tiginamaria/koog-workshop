### 1. Training run recommender
Get the recommended batch size and learning rate for your dataset.

#### Expected deliverables
1. Input: Dataset size (number of samples), model type (CNN, MLP, Transformer)
2. Use some simple rules (if/else) to recommend a batch size, a learning rate, and a number of epochs.
3. Return structured recommendations with explanations.
4. The recommendation can be realistic but fictional.

#### Examples to study
1. StructuredData - Weather Forecast - Structured recommendations
2. Tone - Classification and recommendation patterns
3. SimpleAPI - Basic agent

#### Key patterns to learn
1. Conditional logic in tools
2. Structured output with explanations
3. Using LLM for generating explanations while calculations are deterministic

---

### 2. Model performance calculator 
Calculate ML metrics from predictions and true labels.

#### Expected deliverables
1. Input: Two lists (predicted labels, true labels)
2. Calculate metrics: accuracy, precision, recall, F1-score (one tool for all or a tool for each)
3. Return results in a structured format
4. Explain what each metric means

#### Examples to study
1. Calculator - Simple arithmetic tools
2. StructuredData - Weather Forecast - Structured output pattern
3. SimpleAPI - Basic agent setup

#### Key patterns to learn
1. Creating simple tools with @Tool
2. Using @Serializable data classes
3. requestLLMStructured<MetricsResult>() for typed output
4. Tool descriptions with @LLMDescription

---

### 3. Dataset report generator 
Analyze datasets before training to catch issues early.

#### Expected deliverables
1. Input: CSV file (or path)
2. Read the dataset
3. Calculate basic stats (missing values, data types, row count)
4. Generate a simple text report with findings about the dataset
5. Save the report as a Markdown file

#### Examples to study
1. Calculator - Shows basic tool creation with @Tool annotations
2. SimpleAPI - Basic agent setup and execution patterns
3. SubgraphWithTask - File operations (reading/writing files)

#### Key patterns to learn
1. How to create tools with @Tool and @LLMDescription
2. How to register tools in ToolRegistry
3. Basic agent configuration with AIAgent
4. File I/O operations

---

### 4. Training script generator 
Generate a starter training code for common ML tasks.

#### Expected deliverables
1. Input: user describes the problem ("image classification", "text sentiment analysis")
2. Ask clarifying questions (dataset size, have GPU?)
3. Generate a Python training script
4. Save the script as a .py file

#### Examples to study
1. Guesser - Interactive multi-turn conversation with user
2. SubgraphWithTask - File creation and management
3. SimpleAPI - Chat agent patterns

#### Key patterns to learn
1. Using a tool (e.g. SayToUser) for a user interaction
2. Multi-turn conversation handling
3. Creating files programmatically
4. System prompt design for code generation

---

### 5. Paper insights collector with a web search 
Summarize ML papers and find related work.

#### Expected deliverables
1. Input: paper title or a PDF file
2. Search web for paper (if the title is given)
3. Extract key information
4. Find related papers (if mentioned in the initial paper)
5. Create a structured summary as a Markdown file

#### Examples to study
1. MCP - Google Maps or Playwright MCP - MCP integration patterns
2. Planner - Multi-step task coordination
3. StructuredData - Complex structured output
4. SubgraphWithTask - Multi-step workflows

#### Key patterns to learn
1. MCP server setup and tool discovery
2. Using subgraph for multi-step processes
3. External API/tool integration
4. Sequential task execution
5. Error handling with external services

---

### 6. \<Anything\> tracker 
Log and compare entities systematically.

#### Expected deliverables
1. Store entities in a local JSON database
2. Log entities with a number of params (e.g. metrics, …)
3. Query past entities ("show all …")
4. Compare entities side-by-side
5. Generate comparison reports

#### Examples to study
1. Memory - Memory system with subjects/scopes for persistence
2. Banking - Graph-based routing strategy
3. StructuredData - Structured data storage
4. SubgraphWithTask - File operations for local storage

#### Key patterns to learn
1. install(Memory) feature configuration
2. Memory subjects and scopes organization
3. Graph strategy with routing nodes
4. CRUD operations with tools
5. Persisting data across sessions
