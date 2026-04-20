package com.spacecolony.game.ui;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.spacecolony.game.R;
import com.spacecolony.game.adapter.CrewAdapter;
import com.spacecolony.game.model.Storage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

// Quarters Screen — shows crew at home, move to Simulator or Mission Control
public class QuartersActivity extends AppCompatActivity {

    private CrewAdapter adapter;
    private ArrayAdapter<String> archiveAdapter;
    private final ArrayList<String> archiveNames = new ArrayList<>();
    private File archivesDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewAdapter(Storage.getInstance().getQuartersCrew(), true);
        rv.setAdapter(adapter);
        archivesDir = new File(getFilesDir(), "archives");
        if (!archivesDir.exists()) archivesDir.mkdirs();

        ListView lvArchives = findViewById(R.id.lvArchives);
        archiveAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, archiveNames);
        lvArchives.setAdapter(archiveAdapter);

        findViewById(R.id.btnToSimulator).setOnClickListener(v -> moveSelected("simulator"));
        findViewById(R.id.btnToMission).setOnClickListener(v -> moveSelected("mission"));
        findViewById(R.id.btnSaveArchive).setOnClickListener(v -> saveArchive());
        lvArchives.setOnItemClickListener((parent, view, position, id) -> restoreArchive(archiveNames.get(position)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateData(Storage.getInstance().getQuartersCrew());
        refreshArchiveList();
    }

    private void moveSelected(String dest) {
        java.util.List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(this, "Select crew members first", Toast.LENGTH_SHORT).show();
            return;
        }
        Storage store = Storage.getInstance();
        for (int id : ids) {
            if ("simulator".equals(dest)) store.moveToSimulator(id);
            else store.moveToMissionControl(id);
        }
        String msg = ids.size() + " member(s) moved to " + ("simulator".equals(dest) ? "Simulator" : "Mission Control");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        adapter.updateData(store.getQuartersCrew());
    }

    private void saveArchive() {
        Storage store = Storage.getInstance();
        String snapshot = store.exportSnapshot();
        if (snapshot == null) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            return;
        }
        String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".json";
        File out = new File(archivesDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(out);
            fos.write(snapshot.getBytes("UTF-8"));
            fos.flush();
            fos.close();
            store.clearAllCrewState();
            adapter.updateData(store.getQuartersCrew());
            refreshArchiveList();
            Toast.makeText(this, "Saved: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreArchive(String fileName) {
        File file = new File(archivesDir, fileName);
        if (!file.exists()) {
            Toast.makeText(this, "Archive not found", Toast.LENGTH_SHORT).show();
            refreshArchiveList();
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            fis.close();
            String json = sb.toString();
            boolean ok = Storage.getInstance().importSnapshot(json);
            if (!ok) {
                Toast.makeText(this, "Restore failed", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.updateData(Storage.getInstance().getQuartersCrew());
            Toast.makeText(this, "Restored: " + fileName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Restore failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshArchiveList() {
        archiveNames.clear();
        File[] files = archivesDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            for (File f : files) archiveNames.add(f.getName());
        }
        archiveAdapter.notifyDataSetChanged();
    }
}
