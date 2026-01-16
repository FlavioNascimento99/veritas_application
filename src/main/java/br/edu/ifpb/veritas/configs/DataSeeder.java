package br.edu.ifpb.veritas.configs;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.enums.StatusProcess;
import br.edu.ifpb.veritas.models.*;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Classe responsável por popular o banco de dados
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminService adminService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final SubjectService subjectService;
    private final CollegiateService collegiateService;
    private final ProcessService processService;
    private final MeetingService meetingService;

    @Override
    public void run(String... args) {
        log.info("==========================================");
        log.info("Iniciando população do banco de dados...");
        log.info("==========================================");

        try {
            // Ordem de execução respeitando dependências
            seedAdministrators();
            seedSubjects();
            seedProfessors();
            seedStudents();
            seedCollegiates();
            seedProcesses();
            seedMeetings();

            log.info("==========================================");
            log.info("População do banco concluída com sucesso!");
            log.info("==========================================");
        } catch (Exception e) {
            log.error("Erro ao popular o banco de dados:  {}", e.getMessage(), e);
        }
    }

    /**
     * Cria 2 administradores para testes. 
     * Valida se já existem antes de criar.
     */
    private void seedAdministrators() {
        log.info(">>> Criando Administradores.. .");

        List<Administrator> admins = adminService.listAdmins();
        if (!admins.isEmpty()) {
            log.info("Administradores já existem no banco.  Pulando criação.");
            return;
        }

        try {
            Administrator admin1 = new Administrator();
            admin1.setName("Carlos Alberto da Silva");
            admin1.setLogin("admin");
            admin1.setPassword("admin123");
            admin1.setRegister("ADM001");
            admin1.setPhoneNumber("(83) 98765-4321");
            admin1.setIsActive(true);
            adminService.create(admin1);
            log.info("✓ Administrador criado:  {}", admin1.getName());

            Administrator admin2 = new Administrator();
            admin2.setName("Maria Fernanda Costa");
            admin2.setLogin("maria. admin");
            admin2.setPassword("admin123");
            admin2.setRegister("ADM002");
            admin2.setPhoneNumber("(83) 99876-5432");
            admin2.setIsActive(true);
            adminService.create(admin2);
            log.info("✓ Administrador criado:  {}", admin2.getName());

        } catch (Exception e) {
            log.warn("Erro ao criar administradores: {}", e.getMessage());
        }
    }

    /**
     * Cria assuntos variados para os processos.
     * Mínimo de 10 assuntos com descrições realistas.
     */
    private void seedSubjects() {
        log.info(">>> Criando Assuntos de Processos...");

        List<Subject> existingSubjects = subjectService. findAll();
        if (!existingSubjects.isEmpty()) {
            log.info("Assuntos já existem no banco. Pulando criação.");
            return;
        }

        String[][] subjectsData = {
            {"Trancamento de Matrícula", "Solicitação de trancamento total ou parcial de matrícula no semestre"},
            {"Reabertura de Matrícula", "Pedido de reabertura de matrícula após trancamento ou abandono"},
            {"Dilatação de Prazo", "Extensão do prazo máximo para conclusão do curso"},
            {"Quebra de Pré-requisito", "Solicitação para cursar disciplina sem cumprir pré-requisito"},
            {"Aproveitamento de Disciplina", "Aproveitamento de disciplinas cursadas em outras instituições"},
            {"Revisão de Nota", "Contestação de nota atribuída em disciplina cursada"},
            {"Recurso de Reprovação", "Recurso contra reprovação por falta ou por nota"},
            {"Matrícula Fora do Prazo", "Solicitação de matrícula após encerramento do período regular"},
            {"Inclusão de Disciplina", "Pedido de inclusão de disciplina após início do semestre"},
            {"Cancelamento de Disciplina", "Solicitação de cancelamento de disciplina em andamento"},
            {"Mudança de Turno", "Pedido de transferência de turno (manhã/tarde/noite)"},
            {"Segunda Chamada de Prova", "Solicitação de segunda chamada para avaliação perdida"},
            {"Transferência Interna", "Mudança de curso dentro da mesma instituição"},
            {"Colação de Grau Antecipada", "Pedido de antecipação da cerimônia de colação de grau"}
        };

        for (String[] data : subjectsData) {
            try {
                Subject subject = new Subject();
                subject.setTitle(data[0]);
                subject.setDescription(data[1]);
                subject.setActive(true);
                subjectService.create(subject);
                log.info("✓ Assunto criado: {}", subject. getTitle());
            } catch (Exception e) {
                log. warn("Erro ao criar assunto '{}': {}", data[0], e.getMessage());
            }
        }
    }

    /**
     * Cria professores variados, alguns designados como coordenadores.
     * Mínimo de 10 professores com dados distintos.
     */
    private void seedProfessors() {
        log.info(">>> Criando Professores...");

        List<Professor> existingProfessors = professorService. findAll();
        if (!existingProfessors.isEmpty()) {
            log.info("Professores já existem no banco.  Pulando criação.");
            return;
        }

        String[][] professorsData = {
            {"João Paulo Santos", "joao.santos", "PROF001", "(83) 98111-2233", "true"},  // coordenador
            {"Ana Carolina Oliveira", "ana.oliveira", "PROF002", "(83) 98222-3344", "false"},
            {"Roberto Carlos Lima", "roberto.lima", "PROF003", "(83) 98333-4455", "true"}, // coordenador
            {"Fernanda Souza Costa", "fernanda.costa", "PROF004", "(83) 98444-5566", "false"},
            {"Pedro Henrique Alves", "pedro.alves", "PROF005", "(83) 98555-6677", "false"},
            {"Juliana Martins Rocha", "juliana.rocha", "PROF006", "(83) 98666-7788", "false"},
            {"Marcos Vinícius Silva", "marcos.silva", "PROF007", "(83) 98777-8899", "false"},
            {"Patrícia Gomes Ferreira", "patricia. ferreira", "PROF008", "(83) 98888-9900", "true"}, // coordenador
            {"Lucas Andrade Mendes", "lucas.mendes", "PROF009", "(83) 98999-0011", "false"},
            {"Camila Rodrigues Nunes", "camila.nunes", "PROF010", "(83) 98000-1122", "false"},
            {"Ricardo Barbosa Teixeira", "ricardo.teixeira", "PROF011", "(83) 98111-2234", "false"},
            {"Beatriz Carvalho Dias", "beatriz.dias", "PROF012", "(83) 98222-3345", "false"}
        };

        for (String[] data : professorsData) {
            try {
                Professor professor = new Professor();
                professor. setName(data[0]);
                professor.setLogin(data[1]);
                professor.setPassword("prof123");
                professor.setRegister(data[2]);
                professor.setPhoneNumber(data[3]);
                professor.setCoordinator(Boolean.parseBoolean(data[4]));
                professor.setIsActive(true);
                professorService.create(professor);
                log.info("✓ Professor criado: {} {}", professor.getName(), 
                         professor.getCoordinator() ? "(COORDENADOR)" : "");
            } catch (Exception e) {
                log.warn("Erro ao criar professor '{}': {}", data[0], e.getMessage());
            }
        }
    }

    /**
     * Cria estudantes variados para testes.
     * Mínimo de 10 estudantes com dados distintos.
     */
    private void seedStudents() {
        log.info(">>> Criando Estudantes...");

        List<Student> existingStudents = studentService.findAll();
        if (!existingStudents.isEmpty()) {
            log.info("Estudantes já existem no banco. Pulando criação.");
            return;
        }

        String[][] studentsData = {
            {"Gabriel Silva Santos", "gabriel.santos", "20211001", "(83) 99111-2233"},
            {"Larissa Oliveira Costa", "larissa.costa", "20211002", "(83) 99222-3344"},
            {"Rafael Almeida Lima", "rafael.lima", "20211003", "(83) 99333-4455"},
            {"Amanda Souza Pereira", "amanda.pereira", "20211004", "(83) 99444-5566"},
            {"Thiago Rodrigues Mendes", "thiago.mendes", "20211005", "(83) 99555-6677"},
            {"Carolina Ferreira Dias", "carolina.dias", "20211006", "(83) 99666-7788"},
            {"Felipe Martins Rocha", "felipe.rocha", "20211007", "(83) 99777-8899"},
            {"Bianca Carvalho Nunes", "bianca.nunes", "20211008", "(83) 99888-9900"},
            {"Matheus Barbosa Silva", "matheus.silva", "20211009", "(83) 99999-0011"},
            {"Isabela Gomes Teixeira", "isabela.teixeira", "20211010", "(83) 99000-1122"},
            {"Leonardo Costa Andrade", "leonardo.andrade", "20211011", "(83) 99111-2234"},
            {"Vitória Santos Ribeiro", "vitoria.ribeiro", "20211012", "(83) 99222-3345"},
            {"Bruno Alves Cardoso", "bruno.cardoso", "20211013", "(83) 99333-4456"},
            {"Júlia Fernandes Lima", "julia.lima", "20211014", "(83) 99444-5567"}
        };

        for (String[] data : studentsData) {
            try {
                Student student = new Student();
                student. setName(data[0]);
                student.setLogin(data[1]);
                student.setPassword("aluno123");
                student.setRegister(data[2]);
                student.setPhoneNumber(data[3]);
                student. setIsActive(true);
                studentService.create(student);
                log.info("✓ Estudante criado: {}", student.getName());
            } catch (Exception e) {
                log.warn("Erro ao criar estudante '{}': {}", data[0], e.getMessage());
            }
        }
    }

    /**
     * Cria colegiados com membros (professores) e representantes estudantis.
     * Mínimo de 3 colegiados para cobrir diferentes cenários.
     */
    private void seedCollegiates() {
        log.info(">>> Criando Colegiados.. .");

        List<Collegiate> existingCollegiates = collegiateService.findAll();
        if (!existingCollegiates.isEmpty()) {
            log.info("Colegiados já existem no banco. Pulando criação.");
            return;
        }

        List<Professor> allProfessors = professorService.findAll();
        List<Student> allStudents = studentService.findAll();

        if (allProfessors.isEmpty() || allStudents.isEmpty()) {
            log.warn("Não há professores ou estudantes cadastrados.  Pulando criação de colegiados.");
            return;
        }

        // Colegiado 1: Curso de Sistemas de Informação
        try {
            Collegiate collegiate1 = new Collegiate();
            collegiate1.setDescription("Colegiado do Curso de Bacharelado em Sistemas de Informação");
            collegiate1.setCreatedAt(LocalDateTime.now().minusMonths(6));
            
            // Adiciona 5 professores ao colegiado
            List<Professor> members1 = new ArrayList<>();
            for (int i = 0; i < Math.min(5, allProfessors.size()); i++) {
                members1.add(allProfessors.get(i));
            }
            collegiate1.setCollegiateMemberList(members1);
            
            // Define estudante representante
            if (! allStudents.isEmpty()) {
                collegiate1.setRepresentativeStudent(allStudents. get(0));
            }
            
            collegiateService. create(collegiate1);
            log.info("✓ Colegiado criado: {}", collegiate1.getDescription());
        } catch (Exception e) {
            log.warn("Erro ao criar colegiado 1: {}", e.getMessage());
        }

        // Colegiado 2: Curso de Ciência da Computação
        try {
            Collegiate collegiate2 = new Collegiate();
            collegiate2.setDescription("Colegiado do Curso de Bacharelado em Ciência da Computação");
            collegiate2.setCreatedAt(LocalDateTime.now().minusMonths(12));
            
            // Adiciona professores diferentes
            List<Professor> members2 = new ArrayList<>();
            int startIndex = Math.min(3, allProfessors.size() - 5);
            for (int i = startIndex; i < Math.min(startIndex + 5, allProfessors.size()); i++) {
                members2.add(allProfessors.get(i));
            }
            collegiate2.setCollegiateMemberList(members2);
            
            if (allStudents.size() > 1) {
                collegiate2.setRepresentativeStudent(allStudents.get(1));
            }
            
            collegiateService.create(collegiate2);
            log.info("✓ Colegiado criado: {}", collegiate2.getDescription());
        } catch (Exception e) {
            log.warn("Erro ao criar colegiado 2: {}", e.getMessage());
        }

        // Colegiado 3: Curso de Engenharia de Software
        try {
            Collegiate collegiate3 = new Collegiate();
            collegiate3.setDescription("Colegiado do Curso de Bacharelado em Engenharia de Software");
            collegiate3.setCreatedAt(LocalDateTime.now().minusMonths(3));
            
            // Mix de professores
            List<Professor> members3 = new ArrayList<>();
            for (int i = 0; i < Math.min(6, allProfessors.size()); i += 2) {
                members3.add(allProfessors.get(i));
            }
            collegiate3.setCollegiateMemberList(members3);
            
            if (allStudents.size() > 2) {
                collegiate3.setRepresentativeStudent(allStudents.get(2));
            }
            
            collegiateService.create(collegiate3);
            log.info("✓ Colegiado criado: {}", collegiate3.getDescription());
        } catch (Exception e) {
            log.warn("Erro ao criar colegiado 3: {}", e.getMessage());
        }
    }

    /**
     * Cria processos variados relacionando estudantes, assuntos e professores.
     * Cria processos em diferentes estados (aguardando, em análise, etc.).
     * Mínimo de 15 processos para testes diversos.
     */
    private void seedProcesses() {
        log.info(">>> Criando Processos...");

        List<Process> existingProcesses = processService.findAllProcesses();
        if (!existingProcesses.isEmpty()) {
            log.info("Processos já existem no banco. Pulando criação.");
            return;
        }

        List<Student> allStudents = studentService. findAll();
        List<Subject> allSubjects = subjectService.findAll();
        List<Professor> allProfessors = professorService.findAll();

        if (allStudents.isEmpty() || allSubjects.isEmpty()) {
            log.warn("Não há estudantes ou assuntos cadastrados. Pulando criação de processos.");
            return;
        }

        String[] processTitles = {
            "Solicitação urgente de trancamento",
            "Pedido de reabertura após abandono",
            "Extensão de prazo para TCC",
            "Quebra de pré-requisito para Algoritmos III",
            "Aproveitamento de disciplinas da UFPB",
            "Contestação de nota final em Banco de Dados",
            "Recurso de reprovação por falta",
            "Matrícula extemporânea no semestre 2024.1",
            "Inclusão tardia em disciplina optativa",
            "Cancelamento de disciplina por motivo de saúde",
            "Transferência do turno noturno para o diurno",
            "Segunda chamada de prova por atestado médico",
            "Mudança de curso para Engenharia de Software",
            "Colação de grau antecipada por oportunidade de emprego",
            "Revisão de processo de jubilamento",
            "Solicitação de matrícula em disciplina isolada"
        };

        String[] processDescriptions = {
            "Venho por meio deste solicitar o trancamento de matrícula devido a questões pessoais que impedem minha continuidade neste semestre.",
            "Solicito a reabertura de matrícula, tendo em vista que tive problemas de saúde que me impossibilitaram de acompanhar o semestre anterior.",
            "Peço extensão do prazo para conclusão do curso, pois estou finalizando meu TCC e preciso de mais tempo para concluí-lo adequadamente.",
            "Solicito quebra de pré-requisito para cursar a disciplina, pois tenho conhecimento prévio do conteúdo necessário.",
            "Venho solicitar aproveitamento de disciplinas cursadas em instituição anterior, conforme documentação anexa.",
            "Solicito revisão da nota atribuída, pois acredito haver equívoco na correção da avaliação final.",
            "Apresento recurso contra reprovação por falta, pois tive problemas justificados com atestados médicos.",
            "Solicito matrícula fora do prazo regular devido a problemas com o sistema acadêmico no período de matrículas.",
            "Peço inclusão em disciplina optativa após o início do semestre, pois houve desencontro de informações sobre a oferta.",
            "Solicito cancelamento de disciplina por recomendação médica de redução da carga horária neste semestre.",
            "Venho solicitar mudança de turno para melhor conciliar estudos com atividade profissional.",
            "Solicito segunda chamada de avaliação perdida por motivo de saúde, conforme atestado anexo.",
            "Peço transferência interna de curso por identificação com outra área de conhecimento.",
            "Solicito antecipação da colação de grau devido a oportunidade profissional que exige diploma.",
            "Solicito revisão do processo de jubilamento, pois creio haver equívocos nos cálculos de prazo.",
            "Peço autorização para matrícula em disciplina isolada para complementação de conhecimentos."
        };

        // Cria processos variados
        for (int i = 0; i < Math.min(processTitles.length, 16); i++) {
            try {
                Student student = allStudents.get(i % allStudents.size());
                Subject subject = allSubjects.get(i % allSubjects.size());
                
                Process process = new Process();
                process.setTitle(processTitles[i]);
                process.setDescription(processDescriptions[i]);
                
                // Cria o processo usando o service
                Process createdProcess = processService.createProcess(process, student. getId(), subject.getId());
                
                // Alguns processos já distribuídos (status UNDER_ANALYSIS)
                if (i % 3 == 0 && ! allProfessors.isEmpty()) {
                    Professor rapporteur = allProfessors.get(i % allProfessors.size());
                    processService.distribute(createdProcess. getId(), rapporteur.getId());
                    log.info("✓ Processo criado e distribuído: {} (Relator: {})", 
                             createdProcess.getTitle(), rapporteur.getName());
                } else {
                    log.info("✓ Processo criado (aguardando distribuição): {}", createdProcess.getTitle());
                }
                
            } catch (Exception e) {
                log.warn("Erro ao criar processo {}: {}", i, e.getMessage());
            }
        }
    }

        /**
     * Cria reuniões de colegiado com processos na pauta e professores participantes. 
     * Cria reuniões em diferentes estados (programadas, finalizadas).
     * Mínimo de 5 reuniões para testes.
     */
        private void seedMeetings() {
            log.info(">>> Criando Reuniões.. .");
    
            List<Meeting> existingMeetings = meetingService.findAll();
            if (! existingMeetings.isEmpty()) {
                log.info("Reuniões já existem no banco.  Pulando criação.");
                return;
            }
    
            List<Collegiate> allCollegiates = collegiateService.findAll();
            List<Process> allProcesses = processService.findAllProcesses();
            List<Professor> allProfessors = professorService.findAll();
    
            if (allCollegiates.isEmpty()) {
                log. warn("Não há colegiados cadastrados. Pulando criação de reuniões.");
                return;
            }
    
            if (allProfessors.isEmpty()) {
                log.warn("Não há professores cadastrados. As reuniões serão criadas sem participantes.");
            }
    
            // Reunião 1: Programada para o futuro
            try {
                Meeting meeting1 = new Meeting();
                meeting1.setCollegiate(allCollegiates.get(0));
                meeting1.setCreatedAt(LocalDateTime. now().plusDays(15));
                meeting1.setStatus(MeetingStatus. AGENDADA);
                
                // Adiciona alguns processos na pauta
                if (!allProcesses. isEmpty()) {
                    List<Process> pauta1 = new ArrayList<>();
                    for (int i = 0; i < Math.min(3, allProcesses. size()); i++) {
                        pauta1.add(allProcesses.get(i));
                    }
                    meeting1.setProcesses(pauta1);
                }
                
                // Escala professores para participar da reunião (primeiros 4 professores)
                if (!allProfessors.isEmpty()) {
                    List<Professor> participants1 = new ArrayList<>();
                    for (int i = 0; i < Math.min(4, allProfessors.size()); i++) {
                        participants1.add(allProfessors.get(i));
                    }
                    meeting1.setParticipants(participants1);
                }
                
                meetingService.create(meeting1);
                log.info("✓ Reunião criada (AGENDADA): Colegiado - {} | Participantes: {}", 
                         meeting1.getCollegiate().getDescription(),
                         meeting1.getParticipants().size());
            } catch (Exception e) {
                log.warn("Erro ao criar reunião 1: {}", e.getMessage());
            }
    
            // Reunião 2: Já finalizada (passado)
            if (allCollegiates.size() > 1) {
                try {
                    Meeting meeting2 = new Meeting();
                    meeting2.setCollegiate(allCollegiates.get(1));
                    meeting2.setCreatedAt(LocalDateTime.now().minusDays(30));
                    meeting2.setStatus(MeetingStatus. FINALIZADA);
                    
                    if (allProcesses.size() > 3) {
                        List<Process> pauta2 = new ArrayList<>();
                        for (int i = 3; i < Math. min(6, allProcesses.size()); i++) {
                            pauta2.add(allProcesses.get(i));
                        }
                        meeting2.setProcesses(pauta2);
                    }
                    
                    // Escala professores diferentes (índices 2 a 6)
                    if (allProfessors.size() > 2) {
                        List<Professor> participants2 = new ArrayList<>();
                        int startIdx = 2;
                        for (int i = startIdx; i < Math.min(startIdx + 5, allProfessors.size()); i++) {
                            participants2.add(allProfessors.get(i));
                        }
                        meeting2.setParticipants(participants2);
                    }
                    
                    meetingService.create(meeting2);
                    log. info("✓ Reunião criada (FINALIZADA): Colegiado - {} | Participantes: {}", 
                             meeting2.getCollegiate().getDescription(),
                             meeting2.getParticipants().size());
                } catch (Exception e) {
                    log.warn("Erro ao criar reunião 2: {}", e.getMessage());
                }
            }
    
            // Reunião 3: Programada próxima
            try {
                Meeting meeting3 = new Meeting();
                meeting3.setCollegiate(allCollegiates.get(0));
                meeting3.setCreatedAt(LocalDateTime.now().plusDays(7));
                meeting3.setStatus(MeetingStatus. AGENDADA);
                
                if (allProcesses.size() > 6) {
                    List<Process> pauta3 = new ArrayList<>();
                    for (int i = 6; i < Math. min(9, allProcesses.size()); i++) {
                        pauta3.add(allProcesses.get(i));
                    }
                    meeting3.setProcesses(pauta3);
                }
                
                // Escala mix de professores (índices pares)
                if (! allProfessors.isEmpty()) {
                    List<Professor> participants3 = new ArrayList<>();
                    for (int i = 0; i < allProfessors. size() && participants3.size() < 4; i += 2) {
                        participants3.add(allProfessors.get(i));
                    }
                    meeting3.setParticipants(participants3);
                }
                
                meetingService.create(meeting3);
                log.info("✓ Reunião criada (AGENDADA): Colegiado - {} | Participantes:  {}", 
                         meeting3.getCollegiate().getDescription(),
                         meeting3.getParticipants().size());
            } catch (Exception e) {
                log.warn("Erro ao criar reunião 3: {}", e.getMessage());
            }
    
            // Reunião 4: Finalizada antiga
            if (allCollegiates.size() > 2) {
                try {
                    Meeting meeting4 = new Meeting();
                    meeting4.setCollegiate(allCollegiates.get(2));
                    meeting4.setCreatedAt(LocalDateTime.now().minusDays(60));
                    meeting4.setStatus(MeetingStatus. FINALIZADA);
                    
                    if (allProcesses.size() > 9) {
                        List<Process> pauta4 = new ArrayList<>();
                        for (int i = 9; i < Math. min(11, allProcesses. size()); i++) {
                            pauta4.add(allProcesses.get(i));
                        }
                        meeting4.setProcesses(pauta4);
                    }
                    
                    // Escala últimos professores da lista
                    if (allProfessors.size() > 6) {
                        List<Professor> participants4 = new ArrayList<>();
                        int startIdx = allProfessors. size() - 5;
                        for (int i = startIdx; i < allProfessors. size(); i++) {
                            participants4.add(allProfessors.get(i));
                        }
                        meeting4.setParticipants(participants4);
                    }
                    
                    meetingService.create(meeting4);
                    log.info("✓ Reunião criada (FINALIZADA): Colegiado - {} | Participantes: {}", 
                             meeting4.getCollegiate().getDescription(),
                             meeting4.getParticipants().size());
                } catch (Exception e) {
                    log.warn("Erro ao criar reunião 4: {}", e.getMessage());
                }
            }
    
            // Reunião 5: Programada futuro distante
            if (allCollegiates.size() > 1) {
                try {
                    Meeting meeting5 = new Meeting();
                    meeting5.setCollegiate(allCollegiates.get(1));
                    meeting5.setCreatedAt(LocalDateTime.now().plusDays(30));
                    meeting5.setStatus(MeetingStatus. AGENDADA);
                    
                    if (allProcesses.size() > 11) {
                        List<Process> pauta5 = new ArrayList<>();
                        for (int i = 11; i < Math.min(14, allProcesses. size()); i++) {
                            pauta5.add(allProcesses.get(i));
                        }
                        meeting5.setProcesses(pauta5);
                    }
                    
                    // Escala professores ímpares
                    if (! allProfessors.isEmpty()) {
                        List<Professor> participants5 = new ArrayList<>();
                        for (int i = 1; i < allProfessors. size() && participants5.size() < 5; i += 2) {
                            participants5.add(allProfessors.get(i));
                        }
                        meeting5.setParticipants(participants5);
                    }
                    
                    meetingService. create(meeting5);
                    log.info("✓ Reunião criada (AGENDADA): Colegiado - {} | Participantes: {}", 
                             meeting5.getCollegiate().getDescription(),
                             meeting5.getParticipants().size());
                } catch (Exception e) {
                    log.warn("Erro ao criar reunião 5: {}", e.getMessage());
                }
            }
        }
}