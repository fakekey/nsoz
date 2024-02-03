package com.nsoz.server;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;

import com.nsoz.clan.Clan;
import com.nsoz.db.jdbc.DbManager;
import com.nsoz.model.Char;
import com.nsoz.stall.StallManager;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;

public class NinjaSchool extends WindowAdapter implements ActionListener {
    private Frame frame;
    public static boolean isStop = false;

    public NinjaSchool() {
        try {
            frame = new Frame("Quản lý");
            final Font font = new Font("Segoe UI", Font.PLAIN, 18);

            // Icon
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("icon.png");
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            ImageIcon icon = new ImageIcon(data);
            frame.setIconImage(icon.getImage());
            frame.setSize(480, 504);
            frame.setBackground(Color.BLACK);
            frame.setResizable(false);
            frame.addWindowListener(this);

            // Buttons
            Button btnBaoTri = new Button("Bảo trì");
            btnBaoTri.setFont(font);
            btnBaoTri.setBounds(32, 56, 412, 56);
            btnBaoTri.setActionCommand("maintain");
            btnBaoTri.addActionListener(this);

            Button btnLuuShinwa = new Button("Lưu Shinwa");
            btnLuuShinwa.setFont(font);
            btnLuuShinwa.setBounds(32, 128, 412, 56);
            btnLuuShinwa.setActionCommand("saveShinwa");
            btnLuuShinwa.addActionListener(this);

            Button btnLuuGiaToc = new Button("Lưu dữ liệu gia tộc");
            btnLuuGiaToc.setFont(font);
            btnLuuGiaToc.setBounds(32, 200, 412, 56);
            btnLuuGiaToc.setActionCommand("saveClan");
            btnLuuGiaToc.addActionListener(this);

            Button btnLuuUser = new Button("Lưu dữ liệu người chơi");
            btnLuuUser.setFont(font);
            btnLuuUser.setBounds(32, 272, 412, 56);
            btnLuuUser.setActionCommand("saveUser");
            btnLuuUser.addActionListener(this);

            Button btnRefreshBXH = new Button("Làm mới bảng xếp hạng");
            btnRefreshBXH.setFont(font);
            btnRefreshBXH.setBounds(32, 344, 412, 56);
            btnRefreshBXH.setActionCommand("refreshTop");
            btnRefreshBXH.addActionListener(this);

            Button btnRestartDB = new Button("Khởi động lại Database");
            btnRestartDB.setFont(font);
            btnRestartDB.setBounds(32, 416, 412, 56);
            btnRestartDB.setActionCommand("restartDB");
            btnRestartDB.addActionListener(this);

            // Show
            frame.add(btnBaoTri);
            frame.add(btnLuuShinwa);
            frame.add(btnLuuGiaToc);
            frame.add(btnLuuUser);
            frame.add(btnRefreshBXH);
            frame.add(btnRestartDB);
            // frame.setLocationRelativeTo(null);
            frame.setLocation(32, 32);
            frame.setLayout(null);
            frame.setVisible(true);
        } catch (Exception e) {
            Log.error("Lỗi tạo trình Quản lý!");
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NinjaSchool.saveShinwaAction();
            NinjaSchool.saveClanAction();
            NinjaSchool.saveUserAction();
            NinjaSchool.refreshTopAction();
            if (Server.start) {
                Log.info("Đóng máy chủ.");
                Server.stop();
            }
        }));
        if (!Config.getInstance().load()) {
            Log.error("Vui long kiem tra lai cau hinh!");
            return;
        }
        if (!DbManager.getInstance().start()) {
            return;
        }
        if (NinjaUtils.availablePort(Config.getInstance().getPort())) {
            // new NinjaSchool(); // Manager
            if (!Server.init()) {
                Log.error("Khởi tạo thất bại!");
                return;
            }
            Server.start();
        } else {
            Log.error("Port " + Config.getInstance().getPort() + " đã được sử dụng!");
        }
    }

    public static void saveShinwaAction() {
        if (Server.start) {
            Log.info("Lưu Shinwa");
            StallManager.getInstance().save();
            Log.info("Lưu xong");
        } else {
            Log.info("Mãy chủ chưa bật");
        }
    }

    public static void maintainAction() {
        if (Server.start) {
            if (!isStop) {
                (new Thread(new Runnable() {
                    public void run() {
                        try {
                            Server.maintance();
                            System.exit(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })).start();
            }

        } else {
            Log.info("Máy chủ chưa bật.");
        }
    }

    public static void saveClanAction() {
        Log.info("Lưu dữ liệu gia tộc.");
        List<Clan> clans = Clan.getClanDAO().getAll();
        synchronized (clans) {
            for (Clan clan : clans) {
                Clan.getClanDAO().update(clan);
            }
        }
        Log.info("Lưu xong");
    }

    public static void refreshTopAction() {
        List<Char> chars = ServerManager.getChars();
        for (Char _char : chars) {
            _char.saveData();
        }
        Log.info("Làm mới bảng xếp hạng");
        Ranked.refresh();
    }

    public static void saveUserAction() {
        Log.info("Lưu dữ liệu người chơi");
        List<Char> chars = ServerManager.getChars();
        for (Char _char : chars) {
            try {
                if (_char != null && !_char.isCleaned) {
                    _char.saveData();
                    if (_char.clone != null && !_char.clone.isCleaned) {
                        _char.clone.saveData();
                    }
                    if (_char.user != null && !_char.user.isCleaned) {
                        if (_char.user != null) {
                            _char.user.saveData();
                        }

                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Log.info("Lưu xong");
    }

    public static void restartDBAction() {
        Log.info("Bắt đầu khởi động lại!");
        DbManager.getInstance().shutdown();
        DbManager.getInstance().start();
        Log.info("Khởi động xong!");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("saveShinwa")) {
            NinjaSchool.saveShinwaAction();
        }
        if (e.getActionCommand().equals("maintain")) {
            NinjaSchool.maintainAction();
        }
        if (e.getActionCommand().equals("saveClan")) {
            NinjaSchool.saveClanAction();
        }
        if (e.getActionCommand().equals("refreshTop")) {
            NinjaSchool.refreshTopAction();
        }
        if (e.getActionCommand().equals("saveUser")) {
            NinjaSchool.saveUserAction();
        }
        if (e.getActionCommand().equals("restartDB")) {
            NinjaSchool.restartDBAction();
        }
    }

    public void windowClosing(WindowEvent e) {
        frame.dispose();
        if (Server.start) {
            Log.info("Đóng máy chủ.");
            Server.stop();
            System.exit(0);
        }
    }
}
