## Requisitos funcionais da 2ª etapa

- **RF 4:** O professor consulta todas as reuniões do colegiado, podendo filtrar pelo status dela (finalizada ou agendada).
- **RF 5:** O professor vota pelo deferimento ou indeferimento de um processo, **podendo redigir um texto** com sua justificativa de voto.
- **RF 6:** O professor consulta reuniões agendada para o colegiado nas quais foi escalado para participar.
- **RF 9:** O coordenador pode criar uma sessão do colegiado e definir sua data, sua pauta (processos que serão julgados pelo colegiado) e os membros do colegiado que participarão dela.
- **RF 10:** O coordenador deve iniciar uma sessão de julgamento. **Apenas uma sessão** pode estar iniciada **por vez.**
- **RF 11:** O coordenador deve **apregoar** cada processo da pauta da sessão, indicando para o mesmo como cada membro do colegiado votou (ver tela de condução da sessão). Ao término do apregoamento, o sistema deve calcular automaticamente o resultado do processo (deferido ou indeferido). Se o relator votou pelo deferimento (ou indeferimento) e todos ou a maioria dos membros do colegiado votaram com ele, o processo é considerado deferido (ou indeferido). Caso a maioria tenha votado divergente do relator, o processo terá como resultado o julgamento contrário ao do relator.

> *Apregoar* é designar, dentre os processos da pauta, aquele que está em julgamento no momento.

- **RF 12:** O coordenador deve finalizar uma sessão, caso em que, nenhuma informação acerca dos julgamentos dos processos poderá mais ser alterada.
- **RF 16:** Aluno faz upload (PDF) de texto do requerimento, durante ou após criação do processo. O processo **não pode estar distribuído ainda.**
---
## Requisitos não-funcionais da 2ª etapa

- **RNF 7:** Utilizar layouts e fragmentos para os templates Tymeleaf. 
- **RNF 8:** Utilizar mecanismos de **autenticação e autorização** do Spring Security.
- **RNF 9:** Utilizou paginação em tabelas com reflexo no banco de dados, limitando as consultas ao banco a apenas os registros da página atual.
- **RNF 10:** Utilizou uma **anotação específica** com regra própria para validar a matrícula. 
---
## Implementações opcionais (caso sobre tempo 🤓)

- **RO 1:** Ao criar uma conta, inserir campo de confirmação de senha. No backend, será criado uma outra propriedade para a senha de confirmação, mas a mesma terá a notação @Transient (para que não seja persistida no banco de dados).
- **RO2:** Ao criar uma conta, o sistema deve validar se a senha e a confirmação de senha são iguais.
- **RO3:**
