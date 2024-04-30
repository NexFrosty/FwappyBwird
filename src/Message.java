import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

class Message implements ActionListener {
    final JFrame j;

    Message(JFrame jframe) {
        this.j = jframe;
    }

    public void actionPerformed(ActionEvent jframe) {
        JOptionPane.showMessageDialog(this.j,
                "<html><center><strong>Flappy Bird</strong><br/><i>Powered by Group 2</i></center><br/>MUHAMMAD ASYRAF BIN AMERAN<br/>(2021476772)<br/>NURBALQIS FAHADAH TASSNIEM BINTI NOR MOHAMAD<br/>(2021454598)<br/>MEGAT MUIZZUDDIN BIN ZAINOL<br/>(2021829764)<br/>NIK ADAM RAPHAEL BIN ISMAARIFF<br/>(2021600546)<br/>MUHAMMAD ZULFAZLI BIN NORAMIN<br/>(2021835024)<br/>HAZMI AMZAR BIN AZIZZI<br/>(2021614264)</html>",
                "Credits", -1);
    }
}
