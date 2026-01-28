package br.edu.ifpb.veritas.configs;

import br.edu.ifpb.veritas.enums.MeetingStatus;
import br.edu.ifpb.veritas.enums.VoteType;
import br.edu.ifpb.veritas.models.*;
import br.edu.ifpb.veritas.models.Process;
import br.edu.ifpb.veritas.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * DataSeeder: Classe responsável por popular o banco de dados com dados iniciais
 * para testes e desenvolvimento.
 *
 * Executa automaticamente ao iniciar a aplicação (implementa CommandLineRunner).
 *
 * Ordem de execução:
 * 1. Administradores
 * 2. Assuntos (Subjects)
 * 3. Professores
 * 4. Estudantes
 * 5. Colegiados
 * 6. Processos
 * 7. Reuniões (Meetings)
 * 8. Votos (Votes) - NOVO
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminService adminService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final SubjectService subjectService;
    private final CollegiateService collegiateService;
    private final ProcessService processService;
    private final MeetingService meetingService;
    private final VoteService voteService;  // ← NOVO: injeção do VoteService

    @Override
    public void run(String... args) throws Exception {
        log.info("====== INICIANDO POPULAÇÃO DO BANCO DE DADOS ======");

        seedAdministrators();
        seedSubjects();
        seedProfessors();
        seedStudents();
        seedCollegiates();
        seedProcesses();
        seedMeetings();
        seedVotes();  // ← NOVO: popula votos

        log.info("====== BANCO DE DADOS POPULADO COM SUCESSO ======");
    }

    /**
     * SEED 1: Administradores
     * Cria usuários administradores para gerenciar o sistema.
     */
    private void seedAdministrators() {
        log.info("Populando administradores...");

        if (adminService.listAdmins().isEmpty()) {
            Administrator admin1 = new Administrator();
            admin1.setName("Admin Master");
            admin1.setLogin("admin");
            admin1.setPassword("admin123");  // Será criptografada pelo service
            admin1.setRegister("ADM001");
            admin1.setPhoneNumber("(83) 99999-0001");
            admin1.setIsActive(true);
            adminService.create(admin1);

            Administrator admin2 = new Administrator();
            admin2.setName("Admin Secundário");
            admin2.setLogin("admin2");
            admin2.setPassword("admin456");
            admin2.setRegister("ADM002");
            admin2.setPhoneNumber("(83) 99999-0002");
            admin2.setIsActive(true);
            adminService.create(admin2);

            log.info("✓ {} administradores criados", 2);
        } else {
            log.info("⚠ Administradores já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 2: Assuntos de Processos
     * Cria os tipos de assuntos que podem ser solicitados pelos alunos.
     */
    private void seedSubjects() {
        log.info("Populando assuntos de processos...");

        if (subjectService.findAll().isEmpty()) {
            String[] subjectTitles = {
                    "Reabertura de Matrícula",
                    "Dilatação de Prazo",
                    "Trancamento de Disciplina",
                    "Aproveitamento de Estudos",
                    "Transferência de Curso",
                    "Revisão de Nota",
                    "Quebra de Pré-requisito"
            };

            for (String title : subjectTitles) {
                Subject subject = new Subject();
                subject.setTitle(title);
                subject.setDescription("Assunto referente a " + title.toLowerCase());
                subject.setActive(true);
                subjectService.create(subject);
            }

            log.info("✓ {} assuntos criados", subjectTitles.length);
        } else {
            log.info("⚠ Assuntos já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 3: Professores
     * Cria professores (alguns como coordenadores).
     */
    private void seedProfessors() {
        log.info("Populando professores...");

        if (professorService.findAll().isEmpty()) {
            // Professor 1: Coordenador
            Professor prof1 = new Professor();
            prof1.setName("Dr. João Silva");
            prof1.setLogin("joao.silva");
            prof1.setPassword("prof123");  // Será criptografada
            prof1.setRegister("PROF001");
            prof1.setPhoneNumber("(83) 98888-0001");
            prof1.setCoordinator(true);  // É coordenador
            prof1.setIsActive(true);
            professorService.create(prof1);

            // Professor 2: Membro do colegiado
            Professor prof2 = new Professor();
            prof2.setName("Dra. Maria Santos");
            prof2.setLogin("maria.santos");
            prof2.setPassword("prof123");
            prof2.setRegister("PROF002");
            prof2.setPhoneNumber("(83) 98888-0002");
            prof2.setCoordinator(false);
            prof2.setIsActive(true);
            professorService.create(prof2);

            // Professor 3: Membro do colegiado
            Professor prof3 = new Professor();
            prof3.setName("Dr. Carlos Oliveira");
            prof3.setLogin("carlos.oliveira");
            prof3.setPassword("prof123");
            prof3.setRegister("PROF003");
            prof3.setPhoneNumber("(83) 98888-0003");
            prof3.setCoordinator(false);
            prof3.setIsActive(true);
            professorService.create(prof3);

            // Professor 4: Membro do colegiado
            Professor prof4 = new Professor();
            prof4.setName("Dra. Ana Costa");
            prof4.setLogin("ana.costa");
            prof4.setPassword("prof123");
            prof4.setRegister("PROF004");
            prof4.setPhoneNumber("(83) 98888-0004");
            prof4.setCoordinator(false);
            prof4.setIsActive(true);
            professorService.create(prof4);

            // Professor 5: Membro do colegiado
            Professor prof5 = new Professor();
            prof5.setName("Dr. Pedro Mendes");
            prof5.setLogin("pedro.mendes");
            prof5.setPassword("prof123");
            prof5.setRegister("PROF005");
            prof5.setPhoneNumber("(83) 98888-0005");
            prof5.setCoordinator(false);
            prof5.setIsActive(true);
            professorService.create(prof5);

            log.info("✓ {} professores criados", 5);
        } else {
            log.info("⚠ Professores já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 4: Estudantes
     * Cria alunos que podem criar processos.
     */
    private void seedStudents() {
        log.info("Populando estudantes...");

        if (studentService.findAll().isEmpty()) {
            Student student1 = new Student();
            student1.setName("Lucas Ferreira");
            student1.setLogin("lucas.ferreira");
            student1.setPassword("aluno123");  // Será criptografada
            student1.setRegister("20231001");
            student1.setPhoneNumber("(83) 97777-0001");
            student1.setIsActive(true);
            studentService.create(student1);

            Student student2 = new Student();
            student2.setName("Juliana Souza");
            student2.setLogin("juliana.souza");
            student2.setPassword("aluno123");
            student2.setRegister("20231002");
            student2.setPhoneNumber("(83) 97777-0002");
            student2.setIsActive(true);
            studentService.create(student2);

            Student student3 = new Student();
            student3.setName("Rafael Lima");
            student3.setLogin("rafael.lima");
            student3.setPassword("aluno123");
            student3.setRegister("20231003");
            student3.setPhoneNumber("(83) 97777-0003");
            student3.setIsActive(true);
            studentService.create(student3);

            log.info("✓ {} estudantes criados", 3);
        } else {
            log.info("⚠ Estudantes já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 5: Colegiados
     * Cria colegiados com membros (professores).
     */
    private void seedCollegiates() {
        log.info("Populando colegiados...");

        if (collegiateService.findAll().isEmpty()) {
            // Busca professores já criados
            List<Professor> allProfessors = professorService.findAll();

            if (allProfessors.size() >= 4) {
                Collegiate collegiate1 = new Collegiate();
                collegiate1.setDescription("Colegiado de Ciência da Computação");
                collegiate1.setCreatedAt(LocalDateTime.now().minusMonths(6));
                collegiate1.setCollegiateMemberList(allProfessors.subList(0, 4));  // Primeiros 4 professores
                collegiateService.create(collegiate1);

                log.info("✓ 1 colegiado criado com {} membros", 4);
            } else {
                log.warn("⚠ Não há professores suficientes para criar colegiado.");
            }
        } else {
            log.info("⚠ Colegiados já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 6: Processos
     * Cria processos de alunos.
     */
    private void seedProcesses() {
        log.info("Populando processos...");

        if (processService.findAllProcesses().isEmpty()) {
            List<Student> students = studentService.findAll();
            List<Subject> subjects = subjectService.findAll();
            List<Professor> professors = professorService.findAll();

            if (!students.isEmpty() && !subjects.isEmpty()) {
                // Processo 1: Aguardando distribuição
                Process process1 = new Process();
                process1.setTitle("Solicitação de Reabertura de Matrícula");
                process1.setDescription("Solicito a reabertura de matrícula devido a problemas de saúde.");
                processService.createProcess(process1, students.get(0).getId(), subjects.get(0).getId());

                // Processo 2: Sob análise (já distribuído)
                Process process2 = new Process();
                process2.setTitle("Solicitação de Dilatação de Prazo");
                process2.setDescription("Solicito dilatação de prazo para conclusão do TCC.");
                Process createdProcess2 = processService.createProcess(process2, students.get(1).getId(), subjects.get(1).getId());

                if (!professors.isEmpty()) {
                    processService.distribute(createdProcess2.getId(), professors.get(1).getId());  // Distribui para Prof. Maria
                }

                // Processo 3: Aguardando distribuição
                Process process3 = new Process();
                process3.setTitle("Solicitação de Trancamento de Disciplina");
                process3.setDescription("Solicito trancamento da disciplina de Cálculo II.");
                processService.createProcess(process3, students.get(2).getId(), subjects.get(2).getId());

                // Processo 4: Sob análise
                Process process4 = new Process();
                process4.setTitle("Solicitação de Aproveitamento de Estudos");
                process4.setDescription("Solicito aproveitamento da disciplina cursada em outra instituição.");
                Process createdProcess4 = processService.createProcess(process4, students.get(0).getId(), subjects.get(3).getId());

                if (professors.size() >= 3) {
                    processService.distribute(createdProcess4.getId(), professors.get(2).getId());  // Distribui para Prof. Carlos
                }

                log.info("✓ {} processos criados", 4);
            } else {
                log.warn("⚠ Não há estudantes ou assuntos suficientes para criar processos.");
            }
        } else {
            log.info("⚠ Processos já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 7: Reuniões
     * Cria reuniões com pauta e participantes (REQFUNC 9).
     */
    private void seedMeetings() {
        log.info("Populando reuniões...");

        if (meetingService.findAll().isEmpty()) {
            List<Collegiate> collegiates = collegiateService.findAll();
            List<Process> processes = processService.findAllProcesses();
            List<Professor> professors = professorService.findAll();

            if (!collegiates.isEmpty() && processes.size() >= 2 && professors.size() >= 3) {
                Collegiate collegiate = collegiates.get(0);

                // REUNIÃO 1: Agendada para o futuro (REQFUNC 4 e 6)
                List<Long> processIds1 = Arrays.asList(
                        processes.get(0).getId(),
                        processes.get(1).getId()
                );

                List<Long> participantIds1 = Arrays.asList(
                        professors.get(0).getId(),  // Dr. João (Coordenador)
                        professors.get(1).getId(),  // Dra. Maria
                        professors.get(2).getId()   // Dr. Carlos
                );

                Meeting meeting1 = meetingService.createMeetingWithAgenda(
                        collegiate.getId(),
                        LocalDateTime.now().plusDays(7),  // Agendada para daqui 7 dias
                        processIds1,
                        participantIds1
                );

                log.info("✓ Reunião AGENDADA criada (ID: {}) com {} processos e {} participantes",
                        meeting1.getId(), processIds1.size(), participantIds1.size());

                // REUNIÃO 2: Já finalizada (histórico)
                if (processes.size() >= 4 && professors.size() >= 4) {
                    List<Long> processIds2 = Arrays.asList(
                            processes.get(2).getId(),
                            processes.get(3).getId()
                    );

                    List<Long> participantIds2 = Arrays.asList(
                            professors.get(0).getId(),
                            professors.get(1).getId(),
                            professors.get(2).getId(),
                            professors.get(3).getId()
                    );

                    Meeting meeting2 = meetingService.createMeetingWithAgenda(
                            collegiate.getId(),
                            LocalDateTime.now().minusMonths(1),  // Ocorreu há 1 mês
                            processIds2,
                            participantIds2
                    );

                    // Marca como finalizada (REQFUNC 12)
                    meetingService.updateStatus(meeting2.getId(), MeetingStatus.FINALIZADA);

                    log.info("✓ Reunião FINALIZADA criada (ID: {}) com {} processos e {} participantes",
                            meeting2.getId(), processIds2.size(), participantIds2.size());
                }

                log.info("✓ {} reuniões criadas", 2);
            } else {
                log.warn("⚠ Não há colegiados, processos ou professores suficientes para criar reuniões.");
            }
        } else {
            log.info("⚠ Reuniões já existem no banco. Pulando...");
        }
    }

    /**
     * SEED 8: Votos (NOVO - REQFUNC 5)
     * Cria votos de professores em processos.
     */
    private void seedVotes() {
        log.info("Populando votos...");

        try {
            List<Process> processes = processService.findAllProcesses();
            List<Professor> professors = professorService.findAll();

            if (processes.size() >= 2 && professors.size() >= 3) {
                // Voto 1: Dra. Maria vota DEFERIDO no processo 2 (processo que ela relatou)
                Process process2 = processes.stream()
                        .filter(p -> p.getProcessRapporteur() != null && p.getProcessRapporteur().getId().equals(professors.get(1).getId()))
                        .findFirst()
                        .orElse(null);

                if (process2 != null) {
                    voteService.registerVote(
                            process2.getId(),
                            professors.get(1).getId(),  // Dra. Maria
                            VoteType.DEFERIDO,
                            "Concordo com a solicitação do aluno. Caso bem fundamentado."
                    );
                    log.info("✓ Voto registrado: Dra. Maria votou DEFERIDO no processo {}", process2.getNumber());
                }

                // Voto 2: Dr. João (coordenador) vota DEFERIDO no mesmo processo
                if (process2 != null) {
                    voteService.registerVote(
                            process2.getId(),
                            professors.get(0).getId(),  // Dr. João
                            VoteType.DEFERIDO,
                            "Acompanho o voto da relatora. Situação justifica o deferimento."
                    );
                    log.info("�� Voto registrado: Dr. João votou DEFERIDO no processo {}", process2.getNumber());
                }

                // Voto 3: Dr. Carlos vota INDEFERIDO (divergente)
                if (process2 != null) {
                    voteService.registerVote(
                            process2.getId(),
                            professors.get(2).getId(),  // Dr. Carlos
                            VoteType.INDEFERIDO,
                            "Entendo que não há justificativa suficiente para o pedido."
                    );
                    log.info("✓ Voto registrado: Dr. Carlos votou INDEFERIDO no processo {}", process2.getNumber());
                }

                // Voto 4: Dra. Ana está ausente
                if (process2 != null && professors.size() >= 4) {
                    voteService.registerAbsence(process2.getId(), professors.get(3).getId());  // Dra. Ana
                    log.info("✓ Ausência registrada: Dra. Ana ausente no processo {}", process2.getNumber());
                }

                log.info("✓ {} votos criados", 4);
            } else {
                log.warn("⚠ Não há processos ou professores suficientes para criar votos.");
            }
        } catch (Exception e) {
            log.error("⚠ Erro ao popular votos: {}", e.getMessage());
        }
    }
}