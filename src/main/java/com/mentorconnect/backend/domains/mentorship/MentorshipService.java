package com.mentorconnect.backend.domains.mentorship;

import com.mentorconnect.backend.domains.mentor.MentorProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipProgramRepository programRepository;
    private final GoalRepository goalRepository;
    private final TaskRepository taskRepository;
    private final MentorNoteRepository mentorNoteRepository;

    @Transactional(readOnly = true)
    public List<MentorshipProgram> getStudentPrograms(UUID studentId) {
        return programRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<MentorshipProgram> getMentorPrograms(UUID mentorId) {
        return programRepository.findByMentorId(mentorId);
    }

    @Transactional(readOnly = true)
    public List<Goal> getProgramGoals(UUID programId) {
        return goalRepository.findByProgramId(programId);
    }

    @Transactional
    public Goal createGoal(UUID programId, CreateGoalRequest request) {
        MentorshipProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Mentorship program not found"));

        Goal goal = Goal.builder()
                .program(program)
                .title(request.title())
                .description(request.description())
                .targetDate(request.targetDate())
                .progressPercentage(0)
                .status(Goal.GoalStatus.IN_PROGRESS)
                .build();

        return goalRepository.save(goal);
    }

    @Transactional
    public Task createTask(UUID goalId, CreateTaskRequest request) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        Task task = Task.builder()
                .goal(goal)
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .isCompleted(false)
                .build();

        return taskRepository.save(task);
    }

    @Transactional
    public Task toggleTaskCompletion(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setIsCompleted(!task.getIsCompleted());
        Task savedTask = taskRepository.save(task);

        updateGoalProgress(task.getGoal().getId());

        return savedTask;
    }

    private void updateGoalProgress(UUID goalId) {
        List<Task> tasks = taskRepository.findByGoalId(goalId);
        if (tasks.isEmpty()) {
            return;
        }

        long completedCount = tasks.stream().filter(Task::getIsCompleted).count();
        int percentage = (int) Math.round(((double) completedCount / tasks.size()) * 100);

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));

        goal.setProgressPercentage(percentage);
        if (percentage == 100) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        } else {
            goal.setStatus(Goal.GoalStatus.IN_PROGRESS);
        }
        goalRepository.save(goal);
    }

    @Transactional
    public MentorNote addMentorNote(UUID programId, MentorProfile mentor, AddMentorNoteRequest request) {
        MentorshipProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Mentorship program not found"));

        if (!program.getMentor().getId().equals(mentor.getId())) {
            throw new SecurityException("Unauthorized to add notes to this program");
        }

        MentorNote note = MentorNote.builder()
                .program(program)
                .mentor(mentor)
                .noteContent(request.noteContent())
                .build();

        return mentorNoteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public List<MentorNote> getMentorNotes(UUID programId, UUID mentorId) {
        MentorshipProgram program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Mentorship program not found"));

        if (!program.getMentor().getId().equals(mentorId)) {
            throw new SecurityException("Unauthorized to view private mentor notes");
        }

        return mentorNoteRepository.findByProgramId(programId);
    }
}