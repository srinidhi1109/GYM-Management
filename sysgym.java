import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class WorkoutPlanGUI extends JFrame {
    private JComboBox<String> dayComboBox;
    private JTextArea planTextArea;

    public WorkoutPlanGUI() {
        setTitle("Workout Plan Viewer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        dayComboBox = new JComboBox<>();
        dayComboBox.addItem("Select a Day");
        dayComboBox.addItem("Monday");
        dayComboBox.addItem("Tuesday");
        dayComboBox.addItem("Wednesday");
        dayComboBox.addItem("Thursday");
        dayComboBox.addItem("Friday");
        dayComboBox.addItem("Saturday");
        dayComboBox.addItem("Sunday");

        JButton showPlanButton = new JButton("Show Workout Plan");
        planTextArea = new JTextArea(10, 30);
        planTextArea.setEditable(false);

        panel.add(dayComboBox, BorderLayout.NORTH);
        panel.add(showPlanButton, BorderLayout.CENTER);
        panel.add(new JScrollPane(planTextArea), BorderLayout.SOUTH);

        showPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedDay = (String) dayComboBox.getSelectedItem();
                if (!"Select a Day".equals(selectedDay)) {
                    String workoutPlan = retrieveWorkoutPlan(selectedDay);
                    planTextArea.setText(workoutPlan);
                } else {
                    planTextArea.setText("Please select a day.");
                }
            }
        });

        add(panel);
    }

    private String retrieveWorkoutPlan(String day) {
        String workoutPlan = "Workout plan for " + day + ":\n";

        String jdbcURL = "jdbc:mysql://localhost:3306/gym_workout";
        String username = "root";
        String password = "nks2005nks";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS workout_plan (" +
                    "day_of_week VARCHAR(15) PRIMARY KEY, " +
                    "exercises VARCHAR(255))");

            String query = "SELECT exercises FROM workout_plan WHERE day_of_week = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, day);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                workoutPlan += resultSet.getString("exercises");
            } else {
                workoutPlan += "No plan available for this day.";
            }
        } catch (SQLException ex) {
            workoutPlan = "Error retrieving workout plan: " + ex.getMessage();
        }

        return workoutPlan;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WorkoutPlanGUI gui = new WorkoutPlanGUI();
            gui.setVisible(true);
        });
    }
}