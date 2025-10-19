
import Bulding.*;
import flora.*;

import flora.Flora.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.Iterator;
import java.util.List;


public class GamePanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener, KeyListener {
    private MiniMapPanel miniMapPanel;
    private GameState gameState;
    private JFrame frame; // Referencja do głównego okna
/// / to do FPS by  bylo
    private long lastTime = System.nanoTime();
    private int frames = 0;
    private int fps = 0;
    private long fpsTimer = System.currentTimeMillis();


    public int getFPS() { return fps; }


    private ArrayList<Explosion> explosions; // Lista eksplozji
    private List<HitFlash> hitFlashes = new ArrayList<>();
    private List<BombardmentSequence> activeBombardments = new ArrayList<>();
    private final List<BuildingProgress> buildingProgressList = new ArrayList<>();

    private ArrayList<Soldier> soldiers;
    private ArrayList<SoldierBot> soldierBots;
    private ArrayList<Valkiria> valkirias;
    private ArrayList<Cryopit> cryopits;
    private ArrayList<Minigunner> minigunners;
    private ArrayList<BattleVehicle> battleVehicles;
    private ArrayList<Artylery> artylerys;

    private ArrayList<Crystal> crystals;

    private ArrayList<Flora> floras;
    ArrayList<Flora> obstacles = new ArrayList<>();

    /// to jest do zaznaczania grupowego jednostek
    private ArrayList<Soldier> selectedSoldiers;
    private ArrayList<BuilderVehicle> selectedBuldierVehicles;
    private  ArrayList<Valkiria> selectedValkirias;

    private ArrayList<Harvester> harvesters;
    private ArrayList<Enemy> enemies;
    private ArrayList<EnemyBehemoth> enemyBehemoths;
    private ArrayList<EnemyShooter> enemyShooters;
    private ArrayList<BuilderVehicle> builderVehicles;

    private ArrayList<EnemyToo> enemiesToo; // Nowa lista dla EnemyToo
//    private ArrayList<Marsh> marshes;
    private ArrayList<EnemyHunter> enemyHunters;


    private ArrayList<Bullet> bullets; // Lista pocisków
    private ArrayList<MinigunnerBullet> minigunnerBullets;
    private ArrayList<ArtBullet> artBullets;
    private ArrayList<Object> occupiedTargets = new ArrayList<>();

    private ArrayList<Baracks> baracks;
    private ArrayList<Factory> factories;
    private ArrayList<ResearchCenter> researchCenters;
    private ArrayList<ValkiriaTech> valkiriaTechs;
    private ArrayList<Bulding> buldings;

    private ArrayList<Hive> hives;
    private ArrayList<HiveToo> hiveToos;
    private ArrayList<SteelMine> steelMines;



    private Soldier selectedSoldier;
    private Minigunner selectedMinigunner;
    private BattleVehicle selectedBattleVehicle;
    private Artylery selectedArtylery;
    private Harvester selectedHarvester;
    private Valkiria selectedValkiria;
    private BuilderVehicle selectedBuilderVehicle;
    private Baracks selectedBaracks;
    private SteelMine selectedSteelMines;
    private Factory selectedFactories;
    private Random rand = new Random();
    private String placingBuildingType = "";

    private JLabel countdownLabel; // to jest do tego by odliczalo budowe pojazdow

    private Soldier soldier; // to jest do zapisywania do calego Soldier w savegame i load
/// /////////////////////////////// tu jest do zliczanai zniszczonych wrogow

private int enemyKillPoints = 0; // ile punktów uzyskał gracz (max 50)
    private static final int MAX_KILL_POINTS = 50;

    // Metoda do zliczania punktów po zniszczeniu wroga
    public void addKillPoints(int amount) {
        enemyKillPoints = Math.min(MAX_KILL_POINTS, enemyKillPoints + amount);
    }

    // Pobieranie aktualnych punktów (dla HUD)
    public int getEnemyKillPoints() {
        return enemyKillPoints;
    }

    // Reset punktów (np. po nowej misji)
    public void resetKillPoints() {
        enemyKillPoints = 0;
    }

    /// ///////////////////////////////// to sa do minimapy jednostki by je zwracalo
    public List<Soldier> getSoldiers() {
        return soldiers;
    }
    public List<BuilderVehicle> getBuldierVehgicle() {
        return builderVehicles;
    }
    ///  ////// do szybszego wyciagania info np. ilsoc dla wysweitlania w HUD
    public ArrayList<BuilderVehicle> getBuilderVehicles() {
        return builderVehicles;
    }
    public ArrayList<Soldier> getSoldier(){ return soldiers;
    }
/// /// to do radaru miniMapPanel
    public List<Valkiria> getValkirias() {
        return valkirias;
    }
    public List<ValkiriaTech> getValkiriaTech() {
        return valkiriaTechs;
    }
    public List<SoldierBot> getSoldierBots() {
        return soldierBots;
    }
    public List<Crystal> getCrystals(){return crystals;}
    public List<Artylery> getArtylerys(){return artylerys;}
    public List<Enemy> getEnemies() {
        return enemies;
    }


    public List<EnemyShooter> getenemyShooters() {
        return enemyShooters;
    }
    public List<EnemyBehemoth> getenemyBehemoths() {
        return enemyBehemoths;
    }
    public List<EnemyToo> getenemyToos() {return enemiesToo;}
    public List<Hive> getHives() {return hives;}
    public List<HiveToo> getHiveToos() {return hiveToos;}

    public List<Baracks> getBaracks() {return baracks;}
    public List<Factory> getFactories() {return factories;}
    public List<PowerPlant> getPowerPlants() {return powerPlants;}
    public List<SteelMine> getSteelMines() {return steelMines;}
    public List<ResearchCenter> getResearchCenters() {return researchCenters;}


    private Timer movementTimer;
    private Timer shootingTimer;
    private Timer shootingTimer2;
    private Timer enemyShootingTimer;
    private ArrayList<Projectile> projectiles;
    private ArrayList<ResourcesSteel> resources;
    private ArrayList<PowerPlant> powerPlants;
    private int collectedSteel = 40000; // Przechowuje zebraną ilość stali
    private int totalPower = 0;
    private final int MAX_POWER = 200;


    private JButton btnPowerPlant;
    private JButton btnSteelMine;
    private JButton btnBaracks;
    private JButton btnFactory;
    private JButton btnArtylery2;
    private JButton btnResearch;
    private JButton btnValkiriaTech;

    private JButton btnSoldier;
    private JButton btnValkiria;

    private JButton btnProduceShell;
    private JButton btnFireShell;
    private JButton btnBombardment;

    private JButton btnHarvester;
    private JButton btnBattleVehicle;
    private JButton btnArtylery;
    private JButton btnBuilderVehicle;
    private JButton btnDroneBot;

    private JButton btnDestructionFactory;
    private JButton btnDestructionArty;


    // to do wskazywania miejsca gdzie budowac np powerplant itp na przyszlosc
    private Rectangle placementCursor;

    //do przesowania myszka
    private final int SCROLL_EDGE_SIZE = 20; // ile pikseli od krawędzi reaguje
    private final int SCROLL_SPEED = 20;     // ile pikseli przewija co tick
    private Timer scrollTimer;
    private Point mousePosition;
    private JFrame mainFrame;


    private BufferedImage backgroundImage;

    private long lastSpawnTime = System.currentTimeMillis();
    private static final long SPAWN_INTERVAL = 45000; // resoawn przeciwnika w milisekundach - pojawiaja sie od prawej strony mapy
    private Timer timer;
    private long previousTime = System.currentTimeMillis();


    private boolean showBuilderMenu = false;
    private boolean showBaracksMenu = false;
    private boolean showFactorysMenu = false;
    private boolean showArtysMenu = false;

    private boolean shootingMode = false;

    private boolean bombardmentMode = false;

    //do budowania
    private enum BuildingType {
        POWER_PLANT, STEEL_MINE, BARRACKS, FACTORY,ARTYLERY,RESEARCH, VALKIRIATECH    }

    private boolean isPlacingBuilding = false;
    private BuildingType buildingToPlace = null;

    private boolean isPlacingPowerPlant = false;
    private boolean isPlacingFactory = false;
    private boolean isPlacingValkiriaTech = false;
    private boolean isPlacingSteelMine = false;
    private boolean isPlacingBarracks = false;
    private Point currentMousePosition = null;
    private final int BUILD_RANGE = 170;
    private final int MIN_DISTANCE_FROM_FACTORY = 350;
    private static final int BUILD_SIZE = 80;

    private boolean tooFarFromCrystal = false;

    private JScrollPane scrollPane;
    private MissionManager missionManager;

    // by misja byla mission fail
    private boolean missionFailed = false;
    private long missionFailTime = 0;

    private long missionStartTime = 0;  // czas rozpoczęcia misji
    private final long defendDurationMillis = 15 * 60 * 1000; // 15 minut w milisekundach

    private int destroyedHiveCount = 0;
    private boolean missionCompleted = false;

    public long getMissionStartTime() {
        return missionStartTime;
    }

    public long getDefendDurationMillis() {
        return defendDurationMillis;
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }


    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }



    /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    ///
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
// tu jest load misjii
    public void loadMission(Mission mission) {
//        soldiers.clear();
//        valkirias.clear();
//        builderVehicles.clear();
//        enemies.clear();
//        soldierBots.clear();
//        enemyShooters.clear();
//        hiveToos.clear();
//        enemyHunters.clear();
//        enemiesToo.clear();
//        hives.clear();
//        projectiles.clear();
//        bullets.clear();
//        resources.clear();
//        powerPlants.clear();
//        crystals.clear();

        System.out.println("🔄 Resetuję licznik Hive. Przed: " + destroyedHiveCount);
        destroyedHiveCount = 0;
        System.out.println("🔄 Po resecie: " + destroyedHiveCount);

        //  fabryk
        System.out.println("🔄 Resetuję licznik fabryk. Przed: " + Factory.getTotalFactories());
        Factory.resetFactoryCount();

        System.out.println("🔄 Po resecie: " + Factory.getTotalFactories());
        System.out.println("reset licznik arty tower" + Artylery.getTotalArtys());
        Artylery.resetArtysCount();
        missionCompleted = false;

        for (Point p : mission.soldierPositions) {
            soldiers.add(new Soldier(p.x, p.y));
        }
        for (Point p : mission.valkiriaPositions) {
            valkirias.add(new Valkiria(p.x, p.y));
        }

        for (Point p : mission.builderPositions) {
            builderVehicles.add(new BuilderVehicle(p.x, p.y));
        }
        for (Point p : mission.powerPlantPositions){
            powerPlants.add(new PowerPlant(p.x, p.y));
        }
        for (Point p : mission.crystalPositions){
            crystals.add(new Crystal(p.x, p.y));
        }

        for (int i = 0; i < mission.floraPositions.size(); i++) {
            Point p = mission.floraPositions.get(i);
            String type = mission.floraTypes.get(i);

            Flora flora = switch (type.toLowerCase()) {
                case "marsh" -> new Marsh(p.x, p.y);

                case "marsh2" -> new Marsh2(p.x, p.y);
                case "marsh3" -> new Marsh3(p.x, p.y);
                case "marsh4" -> new Marsh4(p.x, p.y);
                case "marsh5" -> new Marsh5(p.x, p.y);
                case "marsh6" -> new Marsh6(p.x, p.y);
                case "marsh7" -> new Marsh7(p.x, p.y);
                case "marsh8" -> new Marsh8(p.x, p.y);
                case "marsh9" -> new Marsh9(p.x, p.y);
                case "marsh10" -> new Marsh10(p.x, p.y);
                case "marsh11" -> new Marsh11(p.x, p.y);
                default -> null;
            };

            if (flora != null) {
                obstacles.add(flora);
            }
        }


        for (Point p : mission.hiveTooPositions){
            hiveToos.add(new HiveToo(p.x, p.y));
        }
        for ( Point p : mission.barackPositions){
            baracks.add(new Baracks(p.x, p.y));
        }
        for (Point p : mission.factoryPositions){
            factories.add(new Factory(p.x, p.y));
        }


        for (Point p : mission.enemyPositions) {
            enemies.add(new Enemy(p.x, p.y));
        }
        for (Point p : mission.enemyBehemothPositions) {
            enemyBehemoths.add(new EnemyBehemoth(p.x, p.y));
        }

        for (Point p : mission.resourcesPositions) {
            resources.add(new ResourcesSteel(p.x, p.y));
        }

        // ✅ Hive bez nakładania się, by nie pojawily sie w jednym miejscu
        int hiveSize = 100; // Rozmiar Hive (dostosuj jeśli masz inną grafikę)
        ArrayList<Rectangle> hiveRects = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < mission.randomHiveCount; i++) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) {
                int x = mission.hiveSpawnArea.x + rand.nextInt(mission.hiveSpawnArea.width - hiveSize);
                int y = mission.hiveSpawnArea.y + rand.nextInt(mission.hiveSpawnArea.height - hiveSize);
                Rectangle newRect = new Rectangle(x, y, hiveSize, hiveSize);

                boolean overlaps = false;
                for (Rectangle r : hiveRects) {
                    if (newRect.intersects(r)) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    hiveRects.add(newRect);
                    hives.add(new Hive(x, y));
                    placed = true;
                } else {
                    attempts++;
                }
            }

            if (!placed) {
                System.out.println("⚠️ Nie udało się wstawić Hive bez kolizji po 100 próbach.");
            }
            /// /// drugi hive spawner by dal osie w 2 lokacjach je robic
        }
        for (int i = 0; i < mission.randomHiveCount2; i++) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) {
                int x = mission.hiveSpawnArea2.x + rand.nextInt(mission.hiveSpawnArea2.width - hiveSize);
                int y = mission.hiveSpawnArea2.y + rand.nextInt(mission.hiveSpawnArea2.height - hiveSize);
                Rectangle newRect = new Rectangle(x, y, hiveSize, hiveSize);

                boolean overlaps = false;
                for (Rectangle r : hiveRects) {
                    if (newRect.intersects(r)) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    hiveRects.add(newRect);
                    hives.add(new Hive(x, y));
                    placed = true;
                } else {
                    attempts++;
                }
            }

            if (!placed) {
                System.out.println("⚠️ Nie udało się wstawić Hive bez kolizji po 100 próbach.");
            }
        }

        System.out.println("🔹 Misja załadowana: " + mission.name);
        System.out.println("➡️ Typ celu: " + mission.objectiveType);
        System.out.println("➡️ Wymagane Hive do zniszczenia: " + mission.requiredHivesDestroyed);

        updateGame(); // od razu sprawdzanie celu
        missionStartTime = System.currentTimeMillis();
        ResearchCenter.resetCounts();

        // 🔹 Reset collectedSteel tylko przy 3 misji
        if (missionManager.getCurrentMissionIndex() == 2) { // indeks 2 = trzecia misja (licząc od 0)
            System.out.println("🔄 Resetuję zebrany surowiec (collectedSteel) — start 3 misji");
            collectedSteel = 50000;
        }
        // ✅ Ustaw kamerę na pierwsze koszary, jeśli istnieją
        if (baracks != null && !baracks.isEmpty() && scrollPane != null) {
            Baracks first = baracks.get(0);

            // Pozycja środka koszar
            int targetX = first.getX() + first.getWidth() / 2;
            int targetY = first.getY() + first.getHeight() / 2;

            // Przesuń kamerę tak, by Baracks był na środku widoku
            JViewport viewport = scrollPane.getViewport();
            Rectangle viewRect = viewport.getViewRect();

            int scrollX = Math.max(0, targetX - viewRect.width / 2);
            int scrollY = Math.max(0, targetY - viewRect.height / 2);

            viewport.setViewPosition(new Point(scrollX, scrollY));
        }

    }


    private void onMissionCompleted() {
        System.out.println("✅ ✅ onMissionCompleted() — zaczynam wykonanie");
        if (missionCompleted) {
            System.out.println("⚠️ onMissionCompleted() już wywołane, nic nie robię.");
            return;
        }

        System.out.println("🎉 Wywołano onMissionCompleted()!");
        missionCompleted = true;

        JOptionPane.showMessageDialog(this, "Misja ukończona!");

        // 🔹 Sprawdź, czy jest kolejna misja
        if (missionManager.getCurrentMissionIndex() + 1 < missionManager.getTotalMissions()) {
            // 🔹 Przejdź do następnej
            missionManager.nextMission();

            Mission next = missionManager.getCurrentMission();
            System.out.println("➡️ Ładuję kolejną misję: " + next.name);

            clearAllUnitsAndEnemies();
            loadMission(next);
            missionCompleted = false;

        } else {
            // 🔹 Ostatnia misja ukończona → wróć do menu
            JOptionPane.showMessageDialog(this, "🎉 Ukończono wszystkie misje!");

            // Powrót do menu
            SwingUtilities.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                Main.showMainMenu(frame, missionManager);
            });
        }
    }

    // do czyszczenia mapy przed nowa misja
    private void clearAllUnitsAndEnemies() {
        soldiers.clear();
        valkirias.clear();
        crystals.clear();
        soldierBots.clear();
        cryopits.clear();
        minigunners.clear();
        battleVehicles.clear();
        powerPlants.clear();
        artylerys.clear();
        selectedSoldiers.clear();
        selectedBuldierVehicles.clear();
        harvesters.clear();
        enemies.clear();
        enemyShooters.clear();
        enemiesToo.clear();
        enemyHunters.clear();
        builderVehicles.clear();
        bullets.clear();
        minigunnerBullets.clear();
        artBullets.clear();
        baracks.clear();
        researchCenters.clear();
        factories.clear();
        buldings.clear();
        hives.clear();
        hiveToos.clear();
        steelMines.clear();
        explosions.clear();
        projectiles.clear();
//        floras.clear();  gdy to jest dodane to nie laduje 2 misji, o chuj chodzi ?

        // Wyczyść zaznaczenia jednostek
        selectedSoldier = null;
        selectedMinigunner = null;
        selectedBattleVehicle = null;
        selectedArtylery = null;
        selectedHarvester = null;
        selectedValkiria = null;
        selectedBuilderVehicle = null;
        selectedBaracks = null;

        selectedSteelMines = null;
        selectedFactories = null;

        // Wyczyść obiekty zajmujące pola
        occupiedTargets.clear();
    }


    /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////
    /// /// /////////////////////////////////////////////////////////////////////////////////////


    //// to jest odpowiedzalne za to by nie pojawila sie jakas jednostka na sobie i chuj i tak na razie tego nigdzie nie uzywam bo tych jednostek  nie bedzie w grze
    private boolean isCollidingWithOthers(int x, int y, ArrayList<Soldier> soldiers, ArrayList<Minigunner> minigunners, ArrayList<BattleVehicle> battleVehicles) {
        Rectangle newSoldierBounds = new Rectangle(x, y, 42, 34); // Wymiary żołnierza
        Rectangle newMinigunerBounds = new Rectangle(x, y, 20, 20);
        Rectangle newBattleVehicle = new Rectangle(x, y, 50, 50);

        for (Soldier soldier : soldiers) {
            if (newSoldierBounds.intersects(soldier.getBounds())) {
                return true; // Koliduje z innym żołnierzem
            }
        }
        for (Minigunner minigunner : minigunners) {
            if (newMinigunerBounds.intersects(minigunner.getBounds())) {
                return true;
            }
        }

        for (BattleVehicle battleVehicle : battleVehicles) {
            if (newBattleVehicle.intersects(battleVehicle.getBounds())) {
                return true;
            }
        }
        return false; // Pozycja jest wolna
    }


    /// //////// do zaznaczania grupowego
    private Rectangle selectionRectangle = null; // Prostokąt zaznaczenia
    private Point startPoint = null; // Punkt początkowy zaznaczenia

    // to  jest od coraz większego respu enemytoo
    private int waveNumber = 1; // Zaczynamy od fali nr 1
    private int baseEnemyCount = 2; // Początkowa liczba wrogów, tu musisz ustawic ile ich na poczatek sie pojawia podczas respa

    public int getCollectedSteel() {
        return collectedSteel;
    }

    public int getTotalPower() {
        return totalPower;
    }

    private HUDPanel hudPanel; // dodaj pole

    public void setHUDPanel(HUDPanel hudPanel) {
        this.hudPanel = hudPanel;
    }

    // gdy zmieniasz zasoby:
    public void addSteel(int amount) {
        collectedSteel += amount;
        if (hudPanel != null) {
            hudPanel.repaint();
        }
    }


    public GamePanel(JFrame frame, MissionManager missionManager) {

        // to do przesuwania myszka po mapie - by dal osie zamiast strzalkami przesowac mapa
        this.mainFrame = frame;
        this.missionManager = missionManager;

        // Timer sprawdzający pozycję myszy
        scrollTimer = new Timer(20, e -> checkEdgeScrolling());
        scrollTimer.start();

        // Aktualizuj pozycję myszy
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosition = e.getPoint();

                if (isPlacingBuilding && selectedBuilderVehicle != null) {
                    int x = e.getX();
                    int y = e.getY();

                    // Płynna pozycja — bez wyrównania do siatki
                    placementCursor = new Rectangle(x, y, BUILD_SIZE, BUILD_SIZE);
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mousePosition = e.getPoint();
            }
        });


// tu jest okenko glowne gry oraz okienko ktore jest po nacisnieciu ecape
        {
            this.frame = frame;
            this.setPreferredSize(new Dimension(4000, 4000)); // Duża mapa

            setFocusable(true);
            requestFocusInWindow();

            // Obsługa klawisza ESC
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        showPauseMenu();
                    }
                }
            });
        }

        {
            // Ustawiamy większy rozmiar mapy
            this.setPreferredSize(new Dimension(4000, 4000));  // Zwiększamy rozmiar mapy
        }

        addKeyListener(this);
        setFocusable(true); // Wymagane do odbierania zdarzeń klawiatury

        try {
            // Wczytaj obraz tła z plikuF:\projekty JAVA
            backgroundImage = ImageIO.read(new File("F:/projekty JAVA/TerraBionX3/src/background/plener2.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Nie udało się załadować obrazu tła.");
        }


        this.hives = new ArrayList<>();
        this.hiveToos = new ArrayList<>();
        this.cryopits = new ArrayList<>();
        this.powerPlants = new ArrayList<>();
        this.valkiriaTechs = new ArrayList<>();
        this.steelMines = new ArrayList<>();
        this.soldiers = new ArrayList<>();
        this.soldierBots = new ArrayList<>();
        this.valkirias = new ArrayList<>();
        this.minigunners = new ArrayList<>();
        this.battleVehicles = new ArrayList<>();
        this.artylerys = new ArrayList<>();
        this.builderVehicles = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.enemyBehemoths = new ArrayList<>();
        this.enemyShooters = new ArrayList<>();
        this.harvesters = new ArrayList<>();
        this.enemiesToo = new ArrayList<>();
        this.enemyHunters = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.bullets = new ArrayList<>();
        this.minigunnerBullets = new ArrayList<>();
        this.artBullets = new ArrayList<>();
        this.selectedSoldier = null;
        this.selectedMinigunner = null;
        /// do grupowego zaznaczania
        this.selectedSoldiers = new ArrayList<>();
        this.selectedBuldierVehicles = new ArrayList<>();
        this.selectedValkirias = new ArrayList<>();

        this.selectedBattleVehicle = null;
        this.selectedArtylery = null;
        this.selectedHarvester = null;
        this.selectedValkiria = null;
        this.selectedBuilderVehicle = null;
        this.selectedBaracks = null;
        this.selectedSteelMines = null;
        this.selectedFactories = null;
        this.explosions = new ArrayList<>();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.projectiles = new ArrayList<>();
        this.baracks = new ArrayList<>();
        this.factories = new ArrayList<>();
        this.steelMines = new ArrayList<>();
        this.researchCenters = new ArrayList<>();
        this.buldings = new ArrayList<>();

        this.crystals = new ArrayList<>();


        timer = new Timer(1000 / 60, this); // Wywołanie actionPerformed co ~16 ms
        timer.start();
// timer
        Timer productionUpdateTimer = new Timer(1000, e -> {
            for (Factory factory : factories) {
                factory.updateProduction();
                factory.updateUpgrade();
            }
            repaint();
        });
        productionUpdateTimer.start();

        /// ogolna  sekcja z przyciskami, dla przyciskow ktore wystepuja w grze
/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /// /////////////////////////////////////////////////////////////////
/// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// //////////////////////////////////////////////////////////////////
/// /////////////////              \/////////////////////////////////////////////
/// /////////////////  ///////////  ////////////////////////////////
/// /////////////////  ////////////  //////////////////////////////////////////////////////////////////////////////////
/// /////////////////  ////////////  ////////////////////////////////////
/// /////////////////  ///////////  ////////////////////////////////////
/// /////////////////              /////////////////////////////////
        /// /////////  //////////////////////////////////////////////////////////////////////////////////////////////////////
        /// /////////  /////////////////////////////////////////////////////////
        /// /////////  ///////////////////////////////////////////////////////
        /// /////////  ///////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// tu masz przyciski gdy zaznaczysz Buldiera
        setLayout(null); // Ustawienie ręcznego układu dla dodania przycisków

        btnSoldier = new JButton("Phenix Drone");
        btnSoldier.setBounds(10, 50, 120, 30); // Pozycja i rozmiar
        btnSoldier.setVisible(false); // Ukryj przycisk na starcie
        add(btnSoldier);
/// / pamietaj ze przycisk SOLDIER jest teraz przyciskiem do buldierow w barakach!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        btnSoldier.addActionListener(e -> {
            if (selectedBaracks != null) {
                if (collectedSteel >= 2000) {
                    if (builderVehicles.size() >= 5) {
                        System.out.println("Limit FENIX Drone osiągnięty (max 5).");
                        return;
                    }
                    collectedSteel -= 2000; // zabieramy stal
                    selectedBaracks.startProducingBuilder(); // start produkcji
//                    showBaracksMenu = false;
                    updateBaracksMenu();
                    repaint();
                } else {
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 1000 Steel.");
                }
            }
        });



        btnProduceShell = new JButton("Produkuj Pocisk");
        btnProduceShell.setBounds(10, 90, 150, 30);
        btnProduceShell.setVisible(false);
        add(btnProduceShell);

        btnProduceShell.addActionListener(e -> {
            if (selectedBaracks != null) {
                selectedBaracks.startProducingShell();
            }
        });

        btnBombardment = new JButton("Bombardment");
        btnBombardment.setBounds(1680, 380, 150, 30);
        btnBombardment.setVisible(true);
        add(btnBombardment);

        btnBombardment.addActionListener(e -> {
            if (getEnemyKillPoints() >= 12) { // Sprawdź, czy gracz ma wystarczająco punktów
                bombardmentMode = true;
                System.out.println(" Tryb bombardowania aktywny! Kliknij na mapę, aby zrzucić bomby.");
            } else {
                System.out.println(" Za mało punktów! (wymagane 8)");
            }
        });





        btnFireShell = new JButton("Wystrzel Pocisk");
        btnFireShell.setBounds(10, 130, 150, 30);
        btnFireShell.setVisible(false);
        add(btnFireShell);

        btnFireShell.addActionListener(e -> {
            if (selectedBaracks != null && selectedBaracks.getAvailableShells() > 0) {
                // Tryb strzelania aktywny
                shootingMode = true;
                System.out.println("Kliknij na mapę, aby wystrzelić pocisk!");
            }
        });

//        btnDestructionArty.addActionListener(e ->{
//            if (selectedArtylery != null) {
//                if
//            }
//        });

        btnDestructionArty = new JButton("DESTRUCTION");
        btnDestructionArty.setBounds(10, 380, 120, 30);
        btnDestructionArty.setVisible(false);
        add(btnDestructionArty);

        btnDestructionArty.addActionListener(e -> {
            for (Artylery artylery : artylerys) {
                if (artylery.isSelected()) {
                    artylery.destroy();            // niszczya aarte
                    Artylery.decreaseArtysCount(); // zmniejsza licznik
                    explosions.add(new Explosion(artylery.getX(), artylery.getY()));
                    showArtysMenu = false;
                    updateArtysMenu();
                    repaint();
                }
            }

            // Usuń martwe fabryki z listy
            artylerys.removeIf(artylery -> artylery.getBounds().isEmpty() || artylery.takeDamage());
        });

        setLayout(null);

        btnHarvester = new JButton("Harvester");
        btnHarvester.setBounds(10, 90, 120, 30); // Pozycja i rozmiar
        btnHarvester.setVisible(false);
        add(btnHarvester);

        btnDestructionFactory = new JButton("DESTRUCTION");
        btnDestructionFactory.setBounds(10, 380, 120, 30);
        btnDestructionFactory.setVisible(false);
        add(btnDestructionFactory);




        btnArtylery = new JButton("AX-20M");
        btnArtylery.setBounds(10, 130, 120, 30);
        btnArtylery.setVisible(false);
        add(btnArtylery);


        btnBattleVehicle = new JButton("Armored Vehicle");
        btnBattleVehicle.setBounds(10, 170, 120, 30); // Pozycja i rozmiar
        btnBattleVehicle.setVisible(false);
        add(btnBattleVehicle);


        btnBuilderVehicle = new JButton("FENIX Drone");
        btnBuilderVehicle.setBounds(10, 210, 120, 30);
        btnBuilderVehicle.setVisible(false);
        add(btnBuilderVehicle);

        // przycisk do powiekrzania ilsoci botow w resp
        btnDroneBot = new JButton("UPGRADE");
        btnDroneBot.setBounds(10, 290, 120, 30);
        btnDroneBot.setVisible(false);
        add(btnDroneBot);

        // to sluzy do odliczania czasu budowy i wyswietlania
        countdownLabel = new JLabel("");
        countdownLabel.setBounds(140, 130, 60, 30); // Po prawej stronie przycisku
        countdownLabel.setVisible(false);
        add(countdownLabel);

        btnDroneBot.addActionListener(e -> {
            for (Factory factory : factories) {
                if (factory.isSelected()) {
                    factory.upgradeBotLimit();
                }
            }
        });

        btnDestructionFactory.addActionListener(e -> {
            for (Factory factory : factories) {
                if (factory.isSelected()) {
                    factory.destroy();            // niszczya fabrykę
                    Factory.decreaseFactoryCount(); // zmniejsza licznik
                    explosions.add(new Explosion(factory.getX(), factory.getY()));
                    showFactorysMenu = false;
                    updateFactorysMenu();
                    repaint(); // Odśwież panel
                }
            }

            // Usuń martwe fabryki z listy
            factories.removeIf(factory -> factory.getBounds().isEmpty() || factory.takeDamage());
        });


        btnHarvester.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    // Oblicz pozycję jednostki obok Baracks
                    int harvesterX = selectedFactories.getX() + 120;
                    int harvesterY = selectedFactories.getY();

                    // Dodaj jednostkę herv
                    harvesters.add(new Harvester(harvesterX, harvesterY));
                    // Zmniejsz stal o 500 jednostek
                    collectedSteel -= 2000;
                    System.out.println("Dodano harvester.");
                    // Ukryj menu factory
                    showFactorysMenu = false;
                    updateFactorysMenu();
                    repaint(); // Odśwież panel
                } else {
                    // Poinformuj gracza, że nie ma wystarczającej ilości stali
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });

        setLayout(null);

        btnArtylery.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {

                    /// //maksymalna liczba w grze fighterow kontrolowanych przez gracza
                    if (soldiers.size() >= 8) {
                        System.out.println("Limit FENIX Drone osiągnięty (max 8).");
                        return; // Nie buduj więcej
                    }
                    // Oblicz pozycję jednostki obok Factory
                    int soldierX = selectedFactories.getX() + 120;
                    int soldierY = selectedFactories.getY();

                    // Dodaj jednostkę arty

                    // Zmniejsz stal o 500 jednostek
                    collectedSteel -= 2000;
                    selectedFactories.startProducingSoldier(); // start produkcji
                    System.out.println("Dodano AX-20M");
                    // Ukryj menu factory
//                    showFactorysMenu = false;
                    updateFactorysMenu();
                    repaint(); // Odśwież panel
                } else {
                    // Poinformuj gracza, że nie ma wystarczającej ilości stali
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });

        setLayout(null);

        btnValkiria = new JButton("Valkiria");
        btnValkiria.setBounds(10, 50, 120, 30); // Pozycja i rozmiar
        btnValkiria.setVisible(false); // Ukryj przycisk na starcie
        add(btnValkiria);

        btnValkiria.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    if (Valkiria.getTotalValkirias() < Valkiria.getMaxValkirias()) {
                        collectedSteel -= 2000;
                        selectedFactories.startProducingValkiria();
                        updateFactorysMenu();
                        repaint();
                    } else {
                        System.out.println("Potrzebujesz więcej ValkiriaTech!");
                    }
                } else {
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });

        btnBuilderVehicle.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    /// //////// tu jest limit buldierow
                    if (builderVehicles.size() >= 5) {
                        System.out.println("Limit FENIX Drone osiągnięty (max 5).");
                        return; // Nie buduj więcej
                    }

                    int builderVehicleX = selectedFactories.getX() + 120;
                    int builderVehicleY = selectedFactories.getY();

                    collectedSteel -= 2000;

                    selectedFactories.startProduction(10); // nowa metoda fabryki
                    updateFactorysMenu();
                    repaint();

                    Timer countdownTimer = new Timer(1000, new ActionListener() {
                        int secondsLeft = 10;

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            secondsLeft--;
                            if (secondsLeft <= 0) {
                                ((Timer) event.getSource()).stop();

                                // Sprawdzenie ponownie (jeśli coś zmieniło się przez te 10 sekund)
                                if (builderVehicles.size() < 5) {
                                    builderVehicles.add(new BuilderVehicle(builderVehicleX, builderVehicleY));
                                    System.out.println("Dodano Armored Vehicle po 10 sekundach.");
                                } else {
                                    System.out.println("Produkcja anulowana - osiągnięto limit 5 FENIX.");
                                }

                                showFactorysMenu = false;
                                updateFactorysMenu();
                                repaint();
                            }
                        }
                    });
                    countdownTimer.start();
                }
            }
        });
        setLayout(null);

        btnBattleVehicle.addActionListener(e -> {
            if (selectedFactories != null) {
                if (collectedSteel >= 2000) {
                    int battleVehicleX = selectedFactories.getX() + 120;
                    int battleVehicleY = selectedFactories.getY();

                    collectedSteel -= 2000;
                    System.out.println("Produkcja pojazdu opancerzonego... (10 sekund)");
                    selectedFactories.startProduction(10); // nowa metoda fabryki
                    updateFactorysMenu();
                    repaint();

                    Timer countdownTimer = new Timer(1000, new ActionListener() {
                        int secondsLeft = 10;

                        @Override
                        public void actionPerformed(ActionEvent event) {
                            secondsLeft--;
                            if (secondsLeft <= 0) {
                                ((Timer) event.getSource()).stop();
                                battleVehicles.add(new BattleVehicle(battleVehicleX, battleVehicleY));
                                System.out.println("Dodano Armored Vehicle po 10 sekundach.");
                                showFactorysMenu = false;
                                updateFactorysMenu();
                                repaint();
                            }
                        }
                    });
                    countdownTimer.start();

                } else {
                    System.out.println("Nie masz wystarczającej ilości stali! Potrzebujesz 2000 Steel.");
                }
            }
        });

//        btnRocketer.addActionListener(e -> {
//            if (selectedBaracks != null) {
//                System.out.println("Dodano Rocketer (przyszła implementacja).");
//                // Logika dla Rocketer zostanie dodana później
//            }
//        });
//////////////////////////////////////////// przyciski do budowania budynkow u buldiera //////////////
        setLayout(null); // Ustawienie ręcznego układu dla dodania przycisków
        // Tworzenie przycisków
        btnPowerPlant = new JButton("Power Plant");
        btnPowerPlant.setBounds(10, 90, 120, 30);
        btnPowerPlant.setVisible(false);
        add(btnPowerPlant);

        btnSteelMine = new JButton("Steel Mine");
        btnSteelMine.setBounds(10, 130, 120, 30);
        btnSteelMine.setVisible(false);
        btnSteelMine.setEnabled(true);
        add(btnSteelMine);

        btnBaracks = new JButton("Barracks");
        btnBaracks.setBounds(10, 170, 120, 30);
        btnBaracks.setVisible(false);    // Ukryj na starcie
        btnBaracks.setEnabled(true);    // Zablokuj kliknięcie na starcie
        add(btnBaracks);

        btnFactory = new JButton("Factory");
        btnFactory.setBounds(10, 210, 120, 30);
        btnFactory.setVisible(false);
        btnFactory.setEnabled(true);
        add(btnFactory);

        // tym kupuje sie arte przez buldiera
        btnArtylery2 = new JButton("Artylery Tower");
        btnArtylery2.setBounds(10, 250, 120,30);
        btnArtylery2.setVisible(false);
        btnArtylery2.setEnabled(true);
        add(btnArtylery2);

        btnResearch = new JButton("Research Center");
        btnResearch.setBounds(10, 290, 120, 30);
        btnResearch.setVisible(false);
        btnResearch.setEnabled(true);
        add(btnResearch);

        btnValkiriaTech = new JButton("Valkiria Tech");
        btnValkiriaTech.setBounds(10, 330, 120, 30);
        btnValkiriaTech.setVisible(false);
        btnValkiriaTech.setEnabled(true);
        add(btnValkiriaTech);


// Logika dla Power Plant
        btnResearch.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 500 && totalPower >= 100) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.RESEARCH;
                placingBuildingType = "Research Center";
                System.out.println("Wybierz miejsce budowy");
            }
        });

        btnValkiriaTech.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 5000 && totalPower >= 100) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.VALKIRIATECH;
                placingBuildingType = "Valkiria Tech";

            }
        });

        btnArtylery2.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 500 && totalPower >= 50) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.ARTYLERY;
                placingBuildingType = "ARTYLERY";
                System.out.println("Wybierz miejsce budowy artyleria.");
            }
        });
        btnPowerPlant.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 1000) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.POWER_PLANT;
                placingBuildingType = "Power Plant";
                System.out.println("Wybierz miejsce budowy Power Plant.");
            }
        });

        btnSteelMine.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 1500 && totalPower >= 150) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.STEEL_MINE;
                placingBuildingType = "Steel Mine";
                System.out.println("Wybierz miejsce budowy Steel Mine.");
            }
        });

        btnBaracks.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 1500 && totalPower >= 150) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.BARRACKS;
                placingBuildingType = "BARRACKS";
                System.out.println("Wybierz miejsce budowy Baracks.");
            }
        });

        btnFactory.addActionListener(e -> {
            if (selectedBuilderVehicle != null && collectedSteel >= 3000 && totalPower >= 150) {
                isPlacingBuilding = true;
                buildingToPlace = BuildingType.FACTORY;
                placingBuildingType = "Factory";
                System.out.println("Wybierz miejsce budowy Factory.");
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
//        // testowe spawny jednostek itp
//        // Dodajemy 4 losowe pola Stalil Resources Steel
//        Random rand = new Random();
//        for (int i = 0; i < 18; i++) {
//            int x = rand.nextInt(2800); // Losowa pozycja X na mapie
//            int y = rand.nextInt(2800); // Losowa pozycja Y na mapie
//            resources.add(new ResourcesSteel(x, y));
//        }
//        /////////////////////////////////// tu dodaje hives//// od prawej strony
//        for (int i = 0; i < 15; i++) {
//            int x = 900 + rand.nextInt(2500); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
//            int y = rand.nextInt(2500); // Losowa pozycja Y na mapie
//            hives.add(new Hive(x, y));
////                int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
////                int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
//        }
////        for (int i = 0; i < 15; i++) {
////            int x = 1500 + rand.nextInt(2000); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
////            int y = 1200 + rand.nextInt(2000); // Losowa pozycja Y na mapie
////            hives.add(new Hive(x, y));
//////                int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
//////                int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
////        }
//// te respia ciagle
//        for (int i = 0; i < 5; i++) {
//            int x = 900 + rand.nextInt(2000); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
//            int y = rand.nextInt(2000); // Losowa pozycja Y na mapie
//            hiveToos.add(new HiveToo(x, y));
////                int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
////                int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
//        }
////        for (int i = 0; i < 1; i++){
////            int x = 920 + rand.nextInt(811); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
////            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
////            cryopits.add(new Cryopit(x, y));
////        }
//
//
//
//
//        // TU  masz ilosc żołnierzy pojawiajacych sie losowo na start
//        for (int i = 0; i < 5; i++) {
//            int maxAttempts = 100; // Ograniczenie prób, by uniknąć nieskończonej pętli
//            int attempts = 0;
//            int x, y;
//
//            do {
//                // Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
//                int centerXStart = 700 / 4; // 25% szerokości od lewej
//                int centerXEnd = 3 * 700 / 4; // 75% szerokości
//                int centerYStart = 500 / 4; // 25% wysokości od góry
//                int centerYEnd = 3 * 500 / 4; // 75% wysokości
//
//                // Losowa pozycja w obrębie środkowej części
//                x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
//                y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
//
//                attempts++;
//            } while (isCollidingWithOthers(x, y, soldiers, minigunners, battleVehicles) && attempts < maxAttempts);
//
//            if (attempts < maxAttempts) {
//                soldiers.add(new Soldier(x, y)); // Dodanie żołnierza, jeśli znaleziono wolne miejsce
//            } else {
//                System.out.println("Nie znaleziono wolnego miejsca dla nowego żołnierza!");
//            }
//        }
//
//        for (int i = 0; i < 1; i++) {
//            int x = 920 + rand.nextInt(511); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
//            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
//            soldierBots.add(new SoldierBot(x, y));
//        }
////        for (int i = 0; i < 10; i++) {int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się przeciwnik
////            int x, y;
////
////// Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
////            int centerXStart = 700 / 4; // 25% szerokości od lewej
////            int centerXEnd = 3 * 700 / 4; // 75% szerokości
////            int centerYStart = 500 / 4; // 25% wysokości od góry
////            int centerYEnd = 3 * 500 / 4; // 75% wysokości
////
////// Losowa pozycja w obrębie środkowej części
////            x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
////            y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
////
////// Dodaj nowego żołnierza w środkowej części mapy
////            soldiers.add(new Soldier(x, y));
////        }
//        for (int i = 0; i < 2; i++) {
//            int maxAttempts = 100; // Ograniczenie prób, by uniknąć nieskończonej pętli
//            int attempts = 0;
//            int x, y;
//
//            do {
//                // Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
//                int centerXStart = 700 / 4; // 25% szerokości od lewej
//                int centerXEnd = 3 * 700 / 4; // 75% szerokości
//                int centerYStart = 500 / 4; // 25% wysokości od góry
//                int centerYEnd = 3 * 500 / 4; // 75% wysokości
//
//                // Losowa pozycja w obrębie środkowej części
//                x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
//                y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
//
//                attempts++;
//            } while (isCollidingWithOthers(x, y, soldiers, minigunners, battleVehicles) && attempts < maxAttempts);
//
//            if (attempts < maxAttempts) {
//                minigunners.add(new Minigunner(x, y)); // Dodanie żołnierza, jeśli znaleziono wolne miejsce
//            } else {
//                System.out.println("Nie znaleziono wolnego miejsca dla nowego żołnierza!");
//            }
//            //tu masz resp czołgi
//        }
//        for (int i = 0; i < 2; i++) {
//            int maxAttempts = 100; // Ograniczenie prób, by uniknąć nieskończonej pętli
//            int attempts = 0;
//            int x, y;
//
//            do {
//                // Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
//                int centerXStart = 700 / 4; // 25% szerokości od lewej
//                int centerXEnd = 3 * 700 / 4; // 75% szerokości
//                int centerYStart = 500 / 4; // 25% wysokości od góry
//                int centerYEnd = 3 * 500 / 4; // 75% wysokości
//
//                // Losowa pozycja w obrębie środkowej części
//                x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
//                y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
//
//                attempts++;
//            } while (isCollidingWithOthers(x, y, soldiers, minigunners, battleVehicles) && attempts < maxAttempts);
//            if (attempts < maxAttempts) {
//                battleVehicles.add(new BattleVehicle(x, y)); // Dodanie żołnierza, jeśli znaleziono wolne miejsce
//            }
//            else {
//                System.out.println("Nie znaleziono wolnego miejsca dla nowego żołnierza!");
//            }
//        }
////        for (int i = 0; i < 7; i++) {
////            battleVehicles.add(new BattleVehicle(rand.nextInt(600), rand.nextInt(400)));
////        }
//
//
//
////        for (int i = 0; i < 10; i++) {int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się
////            int x, y;
////
////// Środek mapy jako prostokąt obejmujący 50% szerokości i wysokości
////            int centerXStart = 700 / 4; // 25% szerokości od lewej
////            int centerXEnd = 3 * 700 / 4; // 75% szerokości
////            int centerYStart = 500 / 4; // 25% wysokości od góry
////            int centerYEnd = 3 * 500 / 4; // 75% wysokości
////
////// Losowa pozycja w obrębie środkowej części
////            x = rand.nextInt(centerXEnd - centerXStart) + centerXStart;
////            y = rand.nextInt(centerYEnd - centerYStart) + centerYStart;
////
////// Dodaj nowego żołnierza w środkowej części mapy
////            minigunners.add(new Minigunner(x, y));
////        }
//
//        for (int i = 0; i < 3; i++) {
//            builderVehicles.add(new BuilderVehicle(rand.nextInt(600), rand.nextInt(400)));
//        }
//
//
//        for (int i = 0; i < 1; i++) {
//            int x = 920 + rand.nextInt(511); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
//            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
//            enemyHunters.add(new EnemyHunter(x, y));
//        }
//        for (int i = 0; i < 3; i++) {
//            artylerys.add(new Artylery(rand.nextInt(600), rand.nextInt(400)));
//        }
//
//        // Dodajemy 3 losowych wrogów
//        for (int i = 0; i < 1; i++) {
//            int x = 920 + rand.nextInt(511); // Losowa pozycja na mapie w zakresie od 920 pxl + 511
//            int y = rand.nextInt(850); // Losowa pozycja Y na mapie
//            enemies.add(new Enemy(x, y));
//        }
//        for (int i = 0; i < 1; i++){
//            enemyShooters.add(new EnemyShooter(rand.nextInt(1550), rand.nextInt(850)));
//        }
//
//        for (int i = 0; i< 2; i++){
//            harvesters.add(new Harvester(rand.nextInt(700), rand.nextInt(500)));
//        }
//        // inwazja
//// to dodaje w losowych miejscach na krawedzi mapy EnemyToo
//        for (int i = 0; i < 1; i++) {
//            int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się przeciwnik
//            int x = 0, y = 0;
//
////            if (side == 0) {  // Górna część
////                x = rand.nextInt(1400);  // Losowa pozycja na osi X w granicach szerokości planszy
////                y = -20;  // Pozycja na górze poza mapą
////            }
//            ////// tu poniżej musi być else if/////////////////////
//            if (side == 1) {  // Dolna część
//                x = rand.nextInt(1400);  // Losowa pozycja na osi X w granicach szerokości planszy
//                y = 520;  // Pozycja na dole poza mapą
//            } else if (side == 2) {  // Lewa część
//                x = -20;  // Pozycja na lewej krawędzi poza mapą
//                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
//            }
//            if (side == 3) {  // Prawa część
//                x = 720;  // Pozycja na prawej krawędzi poza mapą
//                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
//            }
//
//            enemiesToo.add(new EnemyToo(x, y));
//        }
/////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////


        // Timer do płynnego ruchu/////////////////////////////////////////////////////////////////////////////////////////////////
        Timer movementTimer = new Timer(40, e -> {
            moveSoldiers();
            moveValkirias();
            moveMinigunner();
            moveBattleVehicle();
            moveHarvesters();
            moveBuilderVeicle();
//            moveArtylery();
            updateGame();
            shootEnemiesart();
            shootEnemies();
            minigunnerBulletshoot();
            updateProjectiles();
//            updateCryopits(); // Dodane do obsługi rozrostu Cryopit

            for (Baracks b : baracks) {
                b.updateProduction(); // pociski
                b.updateBuilderProduction(builderVehicles); // builder
            }
            for (Factory f : factories){
                f.updateSoldierProduction(soldiers); // soldier
            }
        });
        movementTimer.start();


//        Timer globalCryopitSpawner = new Timer(30000, e -> spawnRandomCryopit()); // to timer co 30s pojawia sie namapie losy cryopit

// Uruchamiamy timer
//        globalCryopitSpawner.start();

// Timer dla strzelania wrogów
        enemyShootingTimer = new Timer(700, e -> enemyShoot());
        enemyShootingTimer.start();

        Timer resourcesTimer = new Timer(100, e -> {
            updateGameresources();
        });
        resourcesTimer.start();

    }

    /// ////////// to sa koordynaty postajacach cryopitow
//    private void spawnRandomCryopit() {
//        Random rand = new Random();
//
//        int x, y;
//        do {
//            x = rand.nextInt(1920 / 4) * 4; // Losowa pozycja w siatce 4x4
//            y = rand.nextInt(980 / 4) * 4;
//        }
//        while (!Cryopit.isPositionFree(x, y)); // Sprawdzamy, czy miejsce jest wolne
//
//        new Cryopit(x, y); // Tworzymy nowy Cryopit na tej pozycji
//        System.out.println("Nowy Cryopit pojawił się na: " + x + ", " + y);
//    }
    private void checkEdgeScrolling() {
        if (mousePosition == null || getParent() == null || !(getParent() instanceof JViewport viewport)) return;

        Point viewPos = viewport.getViewPosition();
        int newX = viewPos.x;
        int newY = viewPos.y;

        // Pobierz pozycję myszy względem okna aplikacji
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null) return;

        Point mouseLoc = pointerInfo.getLocation(); // pozycja myszy na ekranie
        Point frameLoc = mainFrame.getLocationOnScreen(); // pozycja okna gry na ekranie

        int mouseX = mouseLoc.x - frameLoc.x;
        int mouseY = mouseLoc.y - frameLoc.y;
        int frameWidth = mainFrame.getWidth();
        int frameHeight = mainFrame.getHeight();

        // Teraz sprawdzamy odległość od krawędzi okna gry
        if (mouseX <= SCROLL_EDGE_SIZE) {
            newX -= SCROLL_SPEED;
        } else if (mouseX >= frameWidth - SCROLL_EDGE_SIZE) {
            newX += SCROLL_SPEED;
        }

        if (mouseY <= SCROLL_EDGE_SIZE) {
            newY -= SCROLL_SPEED;
        } else if (mouseY >= frameHeight - SCROLL_EDGE_SIZE) {
            newY += SCROLL_SPEED;
        }

        // Ograniczenie, żeby nie wyjść poza planszę
        newX = Math.max(0, Math.min(newX, getWidth() - viewport.getWidth()));
        newY = Math.max(0, Math.min(newY, getHeight() - viewport.getHeight()));

        viewport.setViewPosition(new Point(newX, newY));
    }


    public void createExplosion(int x, int y) {
        Explosion explosion = new Explosion(x, y);

        explosion.checkEnemyCollision(enemiesToo, enemies, hives, enemyHunters, enemyShooters);
        explosions.add(explosion);
    }

    public void setMiniMapPanel(MiniMapPanel miniMapPanel) {
        this.miniMapPanel = miniMapPanel;
    }


    // menu buldiera
    private void updateBuilderMenu() {
        btnPowerPlant.setVisible(showBuilderMenu);
        btnSteelMine.setVisible(showBuilderMenu);
        btnBaracks.setVisible(showBuilderMenu);
        btnFactory.setVisible(showBuilderMenu);
        btnArtylery2.setVisible(showBuilderMenu);
        btnResearch.setVisible(showBuilderMenu);
        btnValkiriaTech.setVisible(showBuilderMenu);
        repaint(); // Odśwież panel
    }


    private void updateBaracksMenu() {
        btnSoldier.setVisible(showBaracksMenu);
        btnProduceShell.setVisible(showBaracksMenu);
        btnFireShell.setVisible(showBaracksMenu);
        repaint();
    }

    private void stopAllSoldiers() {
        for (Soldier soldier : soldiers) {
            soldier.setTarget(null); // Wyzeruj cel ruchu
        }
        repaint(); // Odśwież ekran, aby odzwierciedlić zmiany
    }

    private void updateFactorysMenu() {
        btnHarvester.setVisible((showFactorysMenu));
        btnValkiria.setVisible((showFactorysMenu));
        btnArtylery.setVisible((showFactorysMenu));
        btnBattleVehicle.setVisible((showFactorysMenu));
        btnBuilderVehicle.setVisible((showFactorysMenu));
        btnDroneBot.setVisible((showFactorysMenu));
        btnDestructionFactory.setVisible((showFactorysMenu));
        repaint();
    }

    private void  updateArtysMenu(){
        btnDestructionArty.setVisible((showArtysMenu));
        repaint();
    }

    private void showPauseMenu() {
        JDialog pauseMenu = new JDialog(frame, "Menu Pauzy", true);
        pauseMenu.setSize(300, 150);
        pauseMenu.setLayout(new GridLayout(4, 1));

        JButton resumeButton = new JButton("Wróć do gry");
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");
        JButton exitButton = new JButton("Wyjdź z gry");

        resumeButton.addActionListener(e -> pauseMenu.dispose());

        // Przycisk zapisu gry
        saveButton.addActionListener(e -> {
//            SaveLoadGame.saveGame(soldiers);  // Wywołanie metody zapisu stanu gry
            System.out.println("Gra została zapisana!");
        });

        // Przycisk wczytywania gry
        loadButton.addActionListener(e -> {
            soldiers.clear();  // Czyścimy aktualną listę żołnierzy przed załadowaniem
//            soldiers.addAll(SaveLoadGame.loadGame());  // Ładujemy żołnierzy z zapisanej gry
            System.out.println("Gra została wczytana!");
        });

        // Przycisk wyjścia z gry
        exitButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(frame, "Czy na pewno chcesz wyjść z gry?", "Potwierdzenie wyjścia", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                System.exit(0);  // Zamyka aplikację
            }
        });

        pauseMenu.add(resumeButton);
        pauseMenu.add(saveButton);
        pauseMenu.add(loadButton);
        pauseMenu.add(exitButton);

        pauseMenu.setLocationRelativeTo(frame);
        pauseMenu.setVisible(true);
    }


    //
    private void enemyShoot() {
        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestSoldier(soldiers);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }
        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestSoldierBot(soldierBots);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestValkiria(valkirias);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestArtylery(artylerys);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestBattleVehicle(battleVehicles);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }


        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestHarvaester(harvesters);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }
        for (Enemy enemy : enemies) {
            Projectile projectile = enemy.shootAtNearestBuilder(builderVehicles);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }
    }

    private long lastCryopitSpawnTime = System.currentTimeMillis();

//    private void updateCryopits() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastCryopitSpawnTime >= 2000) { // Co 2 sekundy
//            for (Cryopit cryopit : cryopits) { // Iterujemy przez istniejące Cryopit
//                cryopit.tryToSpawnNewCryopit();
//            }
//            lastCryopitSpawnTime = currentTime;
//        }
//    }

    // tu jest gdy Enemy strzela w twoje jednostki
    private void updateProjectiles() {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        for (Projectile projectile : projectiles) {
            projectile.move();

            // Sprawdź kolizję z żołnierzami
            for (Soldier soldier : soldiers) {
                if (projectile.checkCollision(soldier)) {
                    toRemove.add(projectile);
                    if (soldier.takeDamage()) {
                        soldiers.remove(soldier); // Usuń żołnierza po trafieniu
                        explosions.add(new Explosion(soldier.getX(), soldier.getY()));
                    }break;
                }
            }
            for (Valkiria valkiria : valkirias) {
                if (projectile.checkCollision(valkiria)) {
                    toRemove.add(projectile);
                    if (valkiria.takeDamage()) {
                        valkirias.remove(valkiria); // Usuń żołnierza po trafieniu
                        explosions.add(new Explosion(valkiria.getX(), valkiria.getY()));
                    }break;
                }
            }
            Iterator<Baracks> iterator2 = baracks.iterator();
            while (iterator2.hasNext()) {
                Baracks baracks = iterator2.next();
                if (projectile.checkCollision(baracks)){
                    toRemove.add(projectile);
                    if (baracks.takeDamage()){
                        missionFailed = true;
                        showMissionFailScreen();
                        iterator2.remove();
                    }
                    break;
                }
            }
            Iterator<SoldierBot> iterator = soldierBots.iterator();
            while (iterator.hasNext()) {
                SoldierBot soldierBot = iterator.next();
                if (projectile.checkCollision(soldierBot)) {
                    toRemove.add(projectile);
                    if (soldierBot.takeDamage()) {
                        iterator.remove(); // usuń tylko jeśli health <= 0
                        explosions.add(new Explosion(soldierBot.getX(), soldierBot.getY()));
                    }
                    break; // zakończ pętlę po trafieniu
                }
            }
            Iterator<BuilderVehicle> it = builderVehicles.iterator();
            while(it.hasNext()) {
                BuilderVehicle bv = it.next();
                if (projectile.checkCollision(bv)) {
                    toRemove.add(projectile);
                    if (bv.takeDamage()) {
                        it.remove();
                        explosions.add(new Explosion(bv.getX(), bv.getY()));
                    }
                    break;
                }
            }
            for (PowerPlant powerPlant : powerPlants) {
                if (projectile.checkCollision(powerPlant)) {
                    toRemove.add(projectile);
                    if (powerPlant.takeDamage()){
                        powerPlants.remove(powerPlant);
                        explosions.add(new Explosion(powerPlant.getX(), powerPlant.getY()));
                    }
                    break;
                }
            }
            for (SteelMine steelMine : steelMines) {
                if (projectile.checkCollision(steelMine)) {
                    toRemove.add(projectile);
                    if (steelMine.takeDamage()){
                        steelMines.remove(steelMine);
                        explosions.add(new Explosion(steelMine.getX(), steelMine.getY()));
                    }
                    break;
                }
            }
            Iterator<Artylery> artyleryIterator = artylerys.iterator();

            while (artyleryIterator.hasNext()) {
                Artylery artylery = artyleryIterator.next();

                if (projectile.checkCollision(artylery)) {
                    toRemove.add(projectile);

                    if (artylery.takeDamage()) {
                        Artylery.decreaseArtysCount(); // 🟢 odejmuje 1
                        artyleryIterator.remove();       // 🧹 usuwa z listy
                        explosions.add(new Explosion(artylery.getX(), artylery.getY()));

                    }
                    break;
                }
            }
            Iterator<Factory> factoryIterator = factories.iterator();

            while (factoryIterator.hasNext()) {
                Factory factory = factoryIterator.next();

                if (projectile.checkCollision(factory)) {
                    toRemove.add(projectile);

                    if (factory.takeDamage()) {
                        Factory.decreaseFactoryCount(); // 🟢 odejmuje 1
                        factoryIterator.remove();       // 🧹 usuwa z listy
                        explosions.add(new Explosion(factory.getX(), factory.getY()));
                    }

                    break;
                }
            }

            for (BattleVehicle battleVehicle : battleVehicles) {
                if (projectile.checkCollision(battleVehicle)) {
                    toRemove.add(projectile);
                    if (battleVehicle.takeDamage()) {
                        battleVehicles.remove(battleVehicle);

                    } // Usuń żołnierza po trafieniu
                    break;
                }

            }
            for (Harvester harvester : harvesters) {
                if (projectile.checkCollision(harvester)) {
                    toRemove.add(projectile);
                    harvesters.remove(harvester);
                    break;
                }
            }

            // Usuń pociski, które wyszły poza planszę
            if (projectile.isOutOfBounds(getWidth(), getHeight())) {
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);
        repaint();
    }

    private void moveSoldiers() {
        for (Soldier soldier : soldiers) {
            Point target = soldier.getTarget();
            if (target != null) {
                // Oblicz przesunięcie względem celu
                int dx = target.x - soldier.getX();
                int dy = target.y - soldier.getY();
                int speed = 3;

                // Ogranicz przesunięcie do maksymalnej prędkości
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                // Aktualizacja kierunku na podstawie przesunięcia
                soldier.updateDirection(new Point(dx, dy));


                // Przesuń żołnierza o obliczone wartości
                soldier.setPosition(soldier.getX() + dx, soldier.getY() + dy, powerPlants, baracks, soldiers, hives, valkirias, battleVehicles);

                // Jeśli żołnierz osiągnął punkt docelowy, usuń cel
                if (dx == 0 && dy == 0) {
                    soldier.setTarget(null);
                }
                soldier.resolveSoftOverlap(soldiers);
                soldier.resolveSoftOverlapWithValkirias(valkirias);
            }
        }
        repaint();
    }


    private void moveValkirias() {
        for (Valkiria valkiria : valkirias) {
            Point target = valkiria.getTarget();
            if (target != null) {
                // Oblicz przesunięcie względem celu
                int dx = target.x - valkiria.getX();
                int dy = target.y - valkiria.getY();
                int speed = 3;

                // Ogranicz przesunięcie do maksymalnej prędkości
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                // Aktualizacja kierunku na podstawie przesunięcia
                valkiria.updateDirection(new Point(dx, dy));


                // Przesuń żołnierza o obliczone wartości
                valkiria.setPosition(valkiria.getX() + dx, valkiria.getY() + dy, powerPlants, baracks, valkirias, soldiers, hives, battleVehicles);

                // Jeśli żołnierz osiągnął punkt docelowy, usuń cel
                if (dx == 0 && dy == 0) {
                    valkiria.setTarget(null);
                }
                valkiria.resolveHardOverlap(valkirias);
                valkiria.resolveSoftOverlapWithValkirias(valkirias);
                valkiria.resolveSoftOverlapWithSoldiers(soldiers);
            }
        }
        repaint();

    }


    private void moveMinigunner() {
        for (Minigunner minigunner : minigunners) {
            Point target = minigunner.getTarget();
            if (target != null) {
                int dx = target.x - minigunner.getX();
                int dy = target.y - minigunner.getY();
                int speed = 3;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                minigunner.setPosition(minigunner.getX() + dx, minigunner.getY() + dy, powerPlants, baracks, soldiers, hives, battleVehicles);

                if (dx == 0 && dy == 0) {
                    minigunner.setTarget(null);

                }
            }
        }
    }

    private void moveBattleVehicle() {
        for (BattleVehicle battleVehicle : battleVehicles) {
            Point target = battleVehicle.getTarget();
            if (target != null) {
                // Oblicz środek pojazdu
                int centerX = battleVehicle.getX() + battleVehicle.getBounds().width / 2;
                int centerY = battleVehicle.getY() + battleVehicle.getBounds().height / 2;
                // Oblicz różnicę między środkiem a celem
                int dx = target.x - centerX;
                int dy = target.y - centerY;
                int speed = 3;
                if (Math.abs(dx) > speed) {
                    dx = dx > 0 ? speed : -speed;
                }
                if (Math.abs(dy) > speed) {
                    dy = dy > 0 ? speed : -speed;
                }
                // Przesuń pojazd – top-left zmienia się, ale środek się przesunie o (dx, dy)
                battleVehicle.setPosition(battleVehicle.getX() + dx, battleVehicle.getY() + dy, powerPlants, baracks, soldiers, hives, battleVehicles);

                // Jeśli różnica jest zerowa (środek się zrównał z celem), usuń cel
                if (dx == 0 && dy == 0) {
                    battleVehicle.setTarget(null);
                }
            }
        }
        repaint();
    }

//    private void moveBattleVehicle() {
//        for (BattleVehicle battleVehicle : battleVehicles) {
//            Point target = battleVehicle.getTarget();
//            if (target != null) {
//                int dx = target.x - battleVehicle.getX();
//                int dy = target.y - battleVehicle.getY();
//                int speed = 3;
//                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
//                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;
//
//
//                battleVehicle.setPosition(battleVehicle.getX() + dx, battleVehicle.getY() + dy, powerPlants);
//
//                // Jeśli osiągnięto punkt docelowy, usuń cel
//                if (dx == 0 && dy == 0) {
//                    battleVehicle.setTarget(null);
//                }
//            }
//        }
//        repaint();
//    }

    private void moveBuilderVeicle() {
        for (BuilderVehicle builderVehicle : builderVehicles) {
            Point target = builderVehicle.getTarget();
            if (target != null) {
                int dx = target.x - builderVehicle.getX();
                int dy = target.y - builderVehicle.getY();
                int speed = 2;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                int nextX = builderVehicle.getX() + dx;
                int nextY = builderVehicle.getY() + dy;

                Rectangle futureBounds = builderVehicle.getAllowedBounds(nextX, nextY);
                boolean canMove = true;

                for (BuilderVehicle other : builderVehicles) {
                    if (builderVehicle == other) continue;

                    if (futureBounds.intersects(other.getAllowedBounds())) {
                        canMove = false; // nie przesuwamy w tej klatce, ale target pozostaje
                        break;
                    }
                }

                if (canMove) {
                    builderVehicle.setPosition(nextX, nextY);
                }

                // Twarda kolizja → teleport
                builderVehicle.resolveHardOverlap(builderVehicles);
            }
        }
        repaint();
    }


    private void moveArtylery() {
        for (Artylery artylery : artylerys) {
            Point target = artylery.getTarget();
            if (target != null) {
                int dx = target.x - artylery.getX();
                int dy = target.y - artylery.getY();
                int speed = 2;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                artylery.setPosition(artylery.getX() + dx, artylery.getY() + dy);

                if (dx == 0 && dy == 0) {
                    artylery.setTarget(null);
                }
            }
        }
        repaint();
    }

    private void moveHarvesters() {
        for (Harvester harvester : harvesters) {
            Point target = harvester.getTarget();
            if (target != null) {
                int dx = target.x - harvester.getX();
                int dy = target.y - harvester.getY();
                int speed = 3;
                if (Math.abs(dx) > speed) dx = dx > 0 ? speed : -speed;
                if (Math.abs(dy) > speed) dy = dy > 0 ? speed : -speed;

                harvester.setPosition(harvester.getX() + dx, harvester.getY() + dy);

                if (dx == 0 && dy == 0) {
                    harvester.setTarget(null);
                }

            }

        }
        repaint();
    }


    private void shootEnemiesart() {
        ArrayList<ArtBullet> artBulletsToRemove = new ArrayList<>();

        for (ArtBullet artBullet : artBullets) {
            artBullet.move();

            // Jeśli pocisk dotarł do celu, twórz eksplozję
            if (artBullet.hasReachedTarget()) {
                createExplosion((int) artBullet.getX(), (int) artBullet.getY()); // Tworzymy eksplozję w miejscu pocisku
                artBulletsToRemove.add(artBullet); // Dodajemy pocisk do usunięcia, ponieważ osiągnął cel
            }
        }

        // Usuwamy pociski, które dotarły do celu
        artBullets.removeAll(artBulletsToRemove);
    }

    private void minigunnerBulletshoot() { // to do strzelania w wrogow
        ArrayList<MinigunnerBullet> minigunnerBulletToRemove = new ArrayList<>();

        for (MinigunnerBullet minigunnerBullet : minigunnerBullets) {
            minigunnerBullet.move();
            if (minigunnerBullet.isOutOfBounds(getWidth(), getHeight())) {
                minigunnerBulletToRemove.add(minigunnerBullet);

            } else {
                for (Enemy enemy : enemies) {
                    if (minigunnerBullet.checkCollision(enemy)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
                        if (enemy.takeDamage()) {
                            enemies.remove(enemy);
                        }
                        break;
                    }
                }
//                boolean hit = false;
                for (Hive hive : hives) {
                    if (minigunnerBullet.checkCollision(hive)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
//                        hit = true;
                        if (hive.takeDamage()) {
                            hives.remove(hive);
                            destroyedHiveCount++;
                        }
                        break;
                    }
                }

                for (EnemyShooter enemyShooter : enemyShooters) {
                    if (minigunnerBullet.checkCollision(enemyShooter)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
//                        hit = true;
                        if (enemyShooter.takeDamage()) {
                            enemyShooters.remove(enemyShooter);
                        }
                        break;
                    }
                }
                for (EnemyHunter enemyHunter : enemyHunters) {
                    if (minigunnerBullet.checkCollision(enemyHunter)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
//                        hit = true;
                        if (enemyHunter.takeDamage()) {
                            enemyHunters.remove(enemyHunter);
                        }
                        break;
                    }
                }

                // Sprawdzanie kolizji z EnemyToo
                for (EnemyToo enemyToo : enemiesToo) {
                    if (minigunnerBullet.checkCollision(enemyToo)) {
                        minigunnerBulletToRemove.add(minigunnerBullet);
                        if (enemyToo.takeDamage()) {
                            enemiesToo.remove(enemyToo);
                        }
                        break;
                    }
                }
            }
        }
        minigunnerBullets.removeAll(minigunnerBulletToRemove);

        repaint();
    }

    /// ///////////////////////////////////////////////////////////////////////////////////////
    private void shootEnemies() { // to do strzelania w wrogow za pomoca bullet
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        for (Bullet bullet : bullets) {
            bullet.move();
            if (bullet.isOutOfBounds(getWidth(), getHeight())) {
                bulletsToRemove.add(bullet);

            } else {
                for (Enemy enemy : enemies) {
                    if (bullet.checkCollision(enemy)) {
                        bulletsToRemove.add(bullet);
                        hitFlashes.add(new HitFlash(enemy.getX() + 15, enemy.getY() + 15));
                        if (enemy.takeDamage2()) {
                            enemies.remove(enemy);
                            addKillPoints(1); // 🟢 +1 punkt za zabicie
                        }
                        break;
                    }
                }

                for (Hive hive : new ArrayList<>(hives)) {
                    if (bullet.checkCollision(hive)) {
                        bulletsToRemove.add(bullet);
                        hitFlashes.add(new HitFlash(hive.getX() + 15, hive.getY() + 15));
                        if (hive.takeDamage()) {
                            hives.remove(hive);
                            destroyedHiveCount++;
                            addKillPoints(3); //  większy wróg = więcej punktów

                            System.out.println("Hive zniszczony! Liczba zniszczonych: " + destroyedHiveCount);
                        }
                        break;
                    }
                }
                for (HiveToo hiveToo : hiveToos) {
                    if (bullet.checkCollision(hiveToo)) {
                        bulletsToRemove.add(bullet);
                        hitFlashes.add(new HitFlash(hiveToo.getX() + 15, hiveToo.getY() + 15));

                        if (hiveToo.takeDamage()) {
                            hiveToos.remove(hiveToo);
                            addKillPoints(3); //  większy wróg = więcej punktów
                        }
                        break;
                    }

                }
                Iterator<EnemyShooter> enemyShooterIterator = enemyShooters.iterator();
                while (enemyShooterIterator.hasNext()) {
                    EnemyShooter enemyShooter = enemyShooterIterator.next();
                    if (bullet.checkCollision(enemyShooter)) {
                        bulletsToRemove.add(bullet);
                        hitFlashes.add(new HitFlash(enemyShooter.getX() + 15, enemyShooter.getY() + 15));
                        if (enemyShooter.takeDamage()) {
                            enemyShooterIterator.remove();
                            addKillPoints(1); // 🟢 +1 punkt
                        }
                        break;
                    }
                }

                for (EnemyHunter enemyHunter : enemyHunters) {
                    if (bullet.checkCollision(enemyHunter)) {
                        bulletsToRemove.add(bullet);

//                        hit = true;
                        if (enemyHunter.takeDamage()) {
                            enemyHunters.remove(enemyHunter);
                        }
                        break;
                    }
                }

                for (EnemyBehemoth enemyBehemoth : enemyBehemoths) {
                    if (bullet.checkCollision(enemyBehemoth)) {
                        bulletsToRemove.add(bullet);
                        hitFlashes.add(new HitFlash(enemyBehemoth.getX() + 15, enemyBehemoth.getY() + 15));
//                        hit = true;
                        if (enemyBehemoth.takeDamage()) {
                            enemyBehemoths.remove(enemyBehemoth);
                            addKillPoints(3); //  większy wróg = więcej punktów
                        }
                        break;
                    }
                }


                // Sprawdzanie kolizji z EnemyToo
                Iterator<EnemyToo> enemyTooIterator = enemiesToo.iterator();
                while (enemyTooIterator.hasNext()) {
                    EnemyToo enemyToo = enemyTooIterator.next();
                    if (bullet.checkCollision(enemyToo)) {
                        bulletsToRemove.add(bullet);
                        hitFlashes.add(new HitFlash(enemyToo.getX() + 15, enemyToo.getY() + 15));

                        if (enemyToo.takeDamage2()) {
                            enemyTooIterator.remove();
                            addKillPoints(1); // 🟢 +1 punkt za zabicie
                        }
                        break;
                    }
                }

            }
        }
        bullets.removeAll(bulletsToRemove);

        repaint();
    }

    private void updateGameresources() {
        for (Harvester harvester : harvesters) { // Iteracja po liście harvesterów
            for (ResourcesSteel resource : resources) { // Iteracja po zasobach
                if (!resource.isDepleted() && harvester.getBounds().intersects(resource.getBounds())) {
                    // Harvester wydobywa zasoby
                    resource.mineResource(2); // Zmniejsz ilość zasobów o 2 na sekundę
                    collectedSteel += 2;     // Zwiększ liczbę zebranych zasobów
                }
            }
        }
        for (SteelMine steelMine : steelMines) {
            for (ResourcesSteel resource : resources) { // Iteracja po zasobach
                if (!resource.isDepleted() && steelMine.getBounds().intersects(resource.getBounds())) {
                    // Harvester wydobywa zasoby
                    resource.mineResource(2); // Zmniejsz ilość zasobów o 2 na sekundę
                    collectedSteel += 2;     // Zwiększ liczbę zebranych zasobów
                    totalPower -=1;
                    if (totalPower < 0) totalPower = 0;         // zabezpieczenie
                }
            }
        }
        // tu zrob by poporstu gdy jest powerplant to dodaje co 1 s 1 produ
        for (PowerPlant powerPlant : powerPlants) {
            if (totalPower < MAX_POWER) {
                totalPower += 1; // każda elektrownia dodaje 1
                if (totalPower > MAX_POWER) {
                    totalPower = MAX_POWER;
                }
            }
        }
        for (ResearchCenter researchCenter : researchCenters){
            collectedSteel -= 1;
            totalPower -=1;
            if (collectedSteel < 0) collectedSteel = 0; // zabezpieczenie
            if (totalPower < 0) totalPower = 0;         // zabezpieczenie
        }
        // 🔽 Pobór zasobów przez ValkiriaTech
        for (ValkiriaTech valkiriaTech : valkiriaTechs) {
            collectedSteel -= 2;  // zużywa 20 na sekunde
            totalPower -= 2;      // zużywa 20 na sekunde
            if (collectedSteel < 0) collectedSteel = 0; // zabezpieczenie
            if (totalPower < 0) totalPower = 0;         // zabezpieczenie
        }
        repaint();
    }

    //update co się dzieje w grze gdy trafi w cos dany pocisk ?
    private void updateGame() {
        // Aktualizacja bombardowań — dodaj nowe eksplozje i od razu sprawdź kolizje
        List<BombardmentSequence> finished = new ArrayList<>();
        for (BombardmentSequence b : activeBombardments) {
            b.update();

            // pobieramy nowe eksplozje wygenerowane w tej aktualizacji
            List<Explosion> newExps = b.getExplosions();
            if (!newExps.isEmpty()) {
                for (Explosion ex : newExps) {
                    // <- TUTAJ od razu sprawdzamy kolizje i zadajemy obrażenia
                    ex.checkEnemyCollision(enemiesToo, enemies, hives, enemyHunters, enemyShooters);

                    // dodajemy eksplozję do głównej listy do rysowania
                    explosions.add(ex);
                }
                newExps.clear(); // czyścimy listę wewnątrz sekwencji
            }

            if (b.isFinished()) finished.add(b);
        }
        activeBombardments.removeAll(finished);


        Mission current = missionManager.getCurrentMission();

        if (current == null) {

            return;
        }
/// /////// to ejst gdy zniszcza baraki to jest koniec misji/gry
        for (Baracks b : baracks) {
            if (b.getHealth() <= 0 && !missionFailed) {
                missionFailed = true;
                missionFailTime = System.currentTimeMillis();
                showMissionFailScreen();
                return;
            }
        }
        if (missionFailed) {
            return;
        }

        // ✅ Logika sprawdzająca warunek zakończenia misji
        if (!missionCompleted) {
            if (current.objectiveType == Mission.ObjectiveType.DESTROY_ALL_HIVES) {


                if (destroyedHiveCount >= current.requiredHivesDestroyed) {
                    System.out.println("✅ Warunek spełniony — wywołuję onMissionCompleted()");
                    onMissionCompleted();
                }

            } else if (current.objectiveType == Mission.ObjectiveType.DEFEND_FOR_TIME) {
                long elapsedTime = System.currentTimeMillis() - missionStartTime;
                long remaining = defendDurationMillis - elapsedTime;



                if (elapsedTime >= defendDurationMillis) {
                    System.out.println("✅ Czas przetrwania upłynął — misja zakończona!");
                    onMissionCompleted();
                }

            }
            else if (current.objectiveType == Mission.ObjectiveType.COLLECT_RESOURCES) {

                // 💎 Nowa logika: sprawdzanie zebranego metalu
                int requiredSteel = 80000; // np. 500 jednostek
                if (collectedSteel >= requiredSteel) {
                    System.out.println("✅ Zebrano wystarczająco stali — misja zakończona!");
                    onMissionCompleted();
                }

            }


            else {
                System.out.println("ℹ️ Inny typ misji — warunek nieobsługiwany.");
            }
        }




        bullets.removeIf(bullet -> bullet.isOutOfBounds(getWidth(), getHeight()) || bullet.isExpired());
        projectiles.removeIf(projectile -> projectile.isOutOfBounds(getWidth(), getHeight()) || projectile.isExpired());
        minigunnerBullets.removeIf(minigunnerBullet -> minigunnerBullet.isOutOfBounds(getWidth(), getHeight()) || minigunnerBullet.isExpired());



        for (EnemyToo enemyToo : enemiesToo) {
            enemyToo.update(soldiers, valkirias, harvesters, baracks, builderVehicles, artylerys, battleVehicles, powerPlants,soldierBots, factories, steelMines, explosions); // Przekazuje listę żołnierzy do śledzenia
        }

        for (EnemyShooter enemyShooter : enemyShooters) {
            enemyShooter.update(soldierBots,soldiers, valkirias, harvesters, builderVehicles, artylerys, baracks, battleVehicles, powerPlants, factories, steelMines);
        }
        for (SoldierBot soldierBot : new ArrayList<>(soldierBots)) {
            soldierBot.update(enemies, enemyShooters, enemiesToo, hives, hiveToos, soldierBots, enemyBehemoths);
        }
        for (EnemyHunter enemyHunter : new ArrayList<>(enemyHunters)){
            enemyHunter.update(soldiers, harvesters, builderVehicles, artylerys, battleVehicles, powerPlants, factories, soldierBots, enemyHunters);
        }
        for (EnemyBehemoth enemyBehemoth : new ArrayList<>(enemyBehemoths)){
            enemyBehemoth.update(soldiers, harvesters, builderVehicles, artylerys, battleVehicles, powerPlants, factories, soldierBots, valkirias, enemyHunters, explosions);
        }
        for (Enemy enemy : enemies) {
            enemy.move();
        }



        repaint();
    }
///  poprostu do gameover
    private void showMissionFailScreen() {
        missionFailTime = System.currentTimeMillis();
        new Thread(() -> {
            try {
                Thread.sleep(3000); // ⏳ Czekaj 3 sekundy
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                Main mainApp = new Main();
                mainApp.main(new String[0]);
            });
        }).start();
    }


    /////////////////////////// to jest ten, no, spawn wrogow co jakis czas na mapie losowo
    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();

//        // Sprawdzanie czy minęło 45 sekund
//        if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
//            spawnEnemyToo();
//            lastSpawnTime = currentTime;
//        }

        repaint();
    }


    private void spawnEnemyToo() {
        Random rand = new Random();

        // Obliczanie liczby wrogów, zwiększanej o 20% z każdą falą
        int numEnemies = (int) Math.ceil(baseEnemyCount * Math.pow(1.20, waveNumber - 1));

        for (int i = 0; i < numEnemies; i++) {
            int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
            int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
            enemiesToo.add(new EnemyToo(x, y)); // Dodajemy nowego wroga do listy enemiesToo
        }

        waveNumber++; // Przechodzimy do następnej fali
    }

//    private void spawnEnemyToo() {
//        Random rand = new Random();
//       // int numEnemies = random.nextInt(11) + 10; // Losowa liczba między 10 a 20
//        for (int i = 0; i < 18; i++) { // Liczba przeciwników do utworzenia
//
//
//                int x = 1620 + rand.nextInt(111); // Pozycja na prawej krawędzi poza mapą (poza szerokością 1600)
//               int y = rand.nextInt(900); // Losowa pozycja na osi Y w granicach wysokości mapy (0-900)
//
//
//
////        for (int i = 0; i < 15; i++) {
////            int side = rand.nextInt(4);  // Losujemy, z której strony pojawi się przeciwnik
////            int x = 0, y = 0;
//
//
////            if (side == 0) {  // Górna część
////                x = rand.nextInt(1700);  // Losowa pozycja na osi X w granicach szerokości planszy
////                y = -20;  // Pozycja na górze poza mapą
////            }
////            // tu poniżej musi być else if/////////////////////
////            if (side == 1) {  // Dolna część
////                x = rand.nextInt(1700);  // Losowa pozycja na osi X w granicach szerokości planszy
////                y = 520;  // Pozycja na dole poza mapą
////            }
////            else if (side == 2) {  // Lewa część
////                x = -20;  // Pozycja na lewej krawędzi poza mapą
////                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
////            }
////             if (side == 3) {  // Prawa część
////                x = 1420;  // Pozycja na prawej krawędzi poza mapą
////                y = rand.nextInt(800);  // Losowa pozycja na osi Y w granicach wysokości planszy
////            }
//
    ////            enemiesToo.add(new EnemyToo(rand.nextInt(1700), rand.nextInt(750)));
//            enemiesToo.add(new EnemyToo(x, y));
//
//        }
//        }
//
//
    private void cancelBuildingPlacement() {
        isPlacingBuilding = false;
        isPlacingPowerPlant = false;
        isPlacingFactory = false;
        isPlacingValkiriaTech = false;
        isPlacingBarracks = false;
        isPlacingSteelMine = false;
        placementCursor = null;
        showBuilderMenu = false;     // <-- dodaj to
        selectedBuilderVehicle = null; // <-- ważne, inaczej builder nadal zaznaczony
        updateBuilderMenu();         // <-- i to, żeby zniknął interfejs
        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) { // Klawisz 'S'
            stopAllSoldiers(); // Wywołaj metodę zatrzymania żołnierzy
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        // Niepotrzebne tutaj
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Niepotrzebne tutaj
    }
    // tu jest to jak budynki moga byc budowane
    @Override
    public void mousePressed(MouseEvent e) {

        /// ///////////// do bombardowania
        /// //////////////////
        if (bombardmentMode && SwingUtilities.isLeftMouseButton(e)) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            if (enemyKillPoints >= 8) {
                enemyKillPoints -= 8; // zużyj 8 punktów
                activeBombardments.add(new BombardmentSequence(mouseX, mouseY));
                bombardmentMode = false; // wyłącz tryb po użyciu
                System.out.println("💥 Bombardowanie rozpoczęte w (" + mouseX + ", " + mouseY + ")");
            } else {
                System.out.println("❌ Brak punktów! Potrzebujesz co najmniej 8.");
                bombardmentMode = false;
            }
            return; // zakończ obsługę kliknięcia, żeby nie zaznaczać jednostek
        }
        /// ///////////
        ///  ////////
        /// //////
        /// //// to jest do budowania budynkow i tu jest ciekawostka ze Flora obstacle : obstacles to sa przeszkody z flora - Marsh itp


        if (isPlacingBuilding && selectedBuilderVehicle != null && SwingUtilities.isLeftMouseButton(e)) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            Point builderPos = selectedBuilderVehicle.getPosition();
            double distance = builderPos.distance(mouseX, mouseY);

            if (distance <= BUILD_RANGE) {
                Rectangle newBuilding = new Rectangle(mouseX, mouseY, BUILD_SIZE, BUILD_SIZE);

                boolean collision = false;
                for (PowerPlant plant : powerPlants)
                    if (plant.getBounds().intersects(newBuilding)) collision = true;

                for (SteelMine mine : steelMines)
                    if (mine.getBounds().intersects(newBuilding)) collision = true;

                for (ResearchCenter researchCenter : researchCenters)
                    if (researchCenter.getBounds().intersects(newBuilding)) collision = true;

                for (ValkiriaTech valkiriaTech : valkiriaTechs)
                    if (valkiriaTech.getBounds().intersects(newBuilding)) collision = true;

                for (Crystal crystal : crystals)
                    if (crystal.getBounds().intersects(newBuilding)) collision = true;

                for (Baracks barack : baracks)
                    if (barack.getBounds().intersects(newBuilding)) collision = true;
                for (Artylery artylery : artylerys)
                    if (artylery.getBounds().intersects(newBuilding))
                        collision = true;

                for (Flora obstacle : obstacles) {
                    if (obstacle.getCollisionBounds().intersects(newBuilding)) {
                        collision = true;
                        break; // wystarczy jedna kolizja
                    }
                }


//                for (Factory factory : factories)
//                    if (factory.getBounds().intersects(newBuilding)) collision = true;
                for (Factory factory : factories) {
                    if (factory.getBounds().intersects(newBuilding)) {
                        collision = true;
                        break;
                    }

                    //  Sprawdzenie minimalnej odległości tylko dla fabryk
                    if (buildingToPlace == BuildingType.FACTORY) {
                        Point existingCenter = new Point(factory.getX() + 55, factory.getY() + 55);
                        Point newCenter = new Point(mouseX + 55, mouseY + 55);
                        // tu jest odleglosc miedzy fabrykami jaka musi byc by mozna bylo postawic
                        if (existingCenter.distance(newCenter) < 350) {
                            collision = true;
                            System.out.println("Zbyt blisko innej fabryki!");
                            break;
                        }
                    }
                    if (buildingToPlace == BuildingType.VALKIRIATECH) {
                        boolean insideCrystalArea = false;
                        for (Crystal crystal : crystals) {
                            if (crystal.getBuildArea(180).contains(newBuilding)) {
                                insideCrystalArea = true;
                                break;
                            }
                        }
                        if (!insideCrystalArea) {
                            collision = true;
                            System.out.println("❌ ValkiriaTech musi być w zasięgu kryształu!");
                        }
                    }
                }


                if (!collision) {
                    long buildTime = 0;
                    String typeName = "";
                    switch (buildingToPlace) {
                        case POWER_PLANT:
                            buildTime = 5000; // 5sek
                            collectedSteel -= 1000;
                            typeName = "PowerPlant";
                            totalPower += PowerPlant.getPowerGenerated();
                            btnSteelMine.setVisible(true);
                            btnSteelMine.setEnabled(true);
                            btnBaracks.setVisible(true);
                            btnBaracks.setEnabled(true);
                            btnFactory.setVisible(true);
                            btnFactory.setEnabled(true);
                            btnArtylery2.setVisible(true);
                            btnArtylery2.setEnabled(true);
                            btnResearch.setVisible(true);
                            btnResearch.setEnabled(true);
                            break;
                        case STEEL_MINE:

                            collectedSteel -= 1500;
                            buildTime = 10000; // 10sekund
                            typeName = "SteelMine";
                            break;
                        case BARRACKS:
                            baracks.add(new Baracks(mouseX, mouseY));
                            collectedSteel -= 1500;
                            totalPower -= 100;
                            break;
                        case RESEARCH:
                            researchCenters.add(new ResearchCenter(mouseX, mouseY));
                            collectedSteel -= 3000;
                            totalPower -= 200;
                            break;

                        case  VALKIRIATECH:
                            valkiriaTechs.add(new ValkiriaTech(mouseX, mouseY));
                            collectedSteel -= 5000; // koszt np.
                            totalPower -= 200;
                            break;

                        case FACTORY:
                            if (Factory.getFactoryCount() < Factory.getMaxFactories()) {
                                factories.add(new Factory(mouseX, mouseY));
                                collectedSteel -= 2500;
                                totalPower -= 150;
                            } else {
                                System.out.println("Nie można zbudować więcej fabryk! Limit osiągnięty.");
                            }
                            break;
                        case ARTYLERY:
                            if (Artylery.getTotalArtys() < Artylery.getMaxArtylerys()) {
                                artylerys.add(new Artylery(mouseX, mouseY));
                                collectedSteel -= 500;
                                totalPower -= 50;
                            } else {
                                System.out.println("❌ Nie można zbudować więcej artylerii! Limit osiągnięty.");
                            }
                            break;
                    }

                    // Dodaj nową budowę do listy postępu
                    buildingProgressList.add(new BuildingProgress(mouseX, mouseY, typeName, buildTime));

                    isPlacingBuilding = false;
                    buildingToPlace = null;
                    placementCursor = null;
                    showBuilderMenu = false;
                    updateBuilderMenu();
                    repaint();
                } else {
                    System.out.println("Kolizja! Nie można budować w tym miejscu.");
                }
            } else {
                System.out.println("Za daleko od buildera.");
            }

            return;
        }

        /// ////////
        /// ///////
        /// ///////
        /// ///////
        if (SwingUtilities.isLeftMouseButton(e)) {
            startPoint = e.getPoint(); // Zapisanie punktu początkowego zaznaczenia
            selectionRectangle = new Rectangle(); // Inicjalizacja prostokąta zaznaczenia

            if (shootingMode && selectedBaracks != null) {
                int targetX = e.getX();
                int targetY = e.getY();

                if (selectedBaracks.useShell()) {
                    artBullets.add(new ArtBullet(
                            selectedBaracks.getX() + 70, // środek koszar
                            selectedBaracks.getY(),
                            targetX, targetY
                    ));
                    System.out.println("Wystrzelono pocisk!");
                }
                shootingMode = false; // wyłączenie trybu strzelania
            }



            // Zaznacz żołnierza
            for (Minigunner minigunner : minigunners) {
                minigunner.setSelected(false);
                if (new Rectangle(minigunner.getX(), minigunner.getY(), 20, 20).contains(e.getPoint())) {
                    selectedMinigunner = minigunner;
                    minigunner.setSelected(true);
                    return;
                }
                else  {
                    minigunner.setSelected(false);
                }
            }

            for (Soldier soldier : soldiers) {
                soldier.setSelected(false);

                if (new Rectangle(soldier.getX(), soldier.getY(), 50, 50).contains(e.getPoint())) {
                    selectedSoldier = soldier;
                    soldier.setSelected(true);
                    return;
                }
//                    else if (selectedSoldier != null){
//                        selectedSoldier.setSelected(false);
//                    }
//                    repaint();
                else {
                    soldier.setSelected(false);
                }
            }
            for (BattleVehicle battleVehicle : battleVehicles) {
                battleVehicle.setSelected(false);

                if (new Rectangle(battleVehicle.getX(), battleVehicle.getY(), 40, 40).contains(e.getPoint())) {
                    selectedBattleVehicle = battleVehicle;
                    battleVehicle.setSelected(true);
                    return;
                }
                else {
                    battleVehicle.setSelected(false);
                }
            }
            // klik lewym zaznacza dziada arty
            for (Artylery artylery : artylerys) {
                artylery.setSelected(false);

                if (new Rectangle(artylery.getX(), artylery.getY(), 40,40).contains(e.getPoint())) {
                    selectedArtylery = artylery;
                    artylery.setSelected(true);
                    showArtysMenu = true;
                    updateArtysMenu();
                    return;
                }
                else  {
                    artylery.setSelected(false);
                }
            }

// gdy klikniedsz lewym na herv, zostaje wybarny
            for (Harvester harvester : harvesters) {
                harvester.setSelected(false);

                if (new Rectangle(harvester.getX(), harvester.getY(), 30, 30).contains(e.getPoint())) {
                    selectedHarvester = harvester;
                    harvester.setSelected(true);

                    return;

                } else {
                    harvester.setSelected(false);
                }
            }
            for (Valkiria valkiria : valkirias) {
                valkiria.setSelected(false);

                if (new Rectangle(valkiria.getX(), valkiria.getY(), 50, 50).contains(e.getPoint())) {
                    selectedValkiria = valkiria;
                    valkiria.setSelected(true);

                    return;

                } else {
                    valkiria.setSelected(false);
                }
            }

            for (BuilderVehicle builderVehicle : builderVehicles){
                builderVehicle.setSelected(false);

                if (new Rectangle(builderVehicle.getX(), builderVehicle.getY(),50, 50).contains(e.getPoint())){
                    selectedBuilderVehicle = builderVehicle;
                    builderVehicle.setSelected(true);
                    System.out.println("Zaznaczono BuilderVehicle.");
                    showBuilderMenu = true; // Pokaż menu budowy
                    updateBuilderMenu();
                    return;
                } else {
                    builderVehicle.setSelected(false);
                }
            }
            for (Factory factory : factories){
                factory.setSelected(false);

                if (new Rectangle(factory.getX(), factory.getY(),140, 140).contains(e.getPoint())){
                    selectedFactories = factory;
                    factory.setSelected(true);
                    System.out.println("Zaznaczono Factory.");
                    showFactorysMenu = true; // Pokaż menu produkcji factory
                    updateFactorysMenu();
                    return;
                } else {
                    factory.setSelected(false);
                }
            }
            for (Baracks baracks : baracks){
                baracks.setSelected(false);

                if (new Rectangle(baracks.getX(), baracks.getY(), 140, 140 ).contains(e.getPoint())){
                    selectedBaracks = baracks;
                    baracks.setSelected(true);
                    System.out.println("Zaznaczono baracks");
                    showBaracksMenu = true; // pokaz menu produkcji
                    updateBaracksMenu();
                    return;
                } else {
                    baracks.setSelected(false);
                }
            }
            boolean unitSelected = false;
            for (Soldier soldier : soldiers) {
                if (new Rectangle(soldier.getX(), soldier.getY(), 20, 20).contains(e.getPoint())) {
                    selectedSoldier = soldier;
                    soldier.setSelected(true);
                    unitSelected = true;
                    break;
                }
            }
            // Jeśli żadna jednostka nie została zaznaczona, pozwól na przeciąganie
            if (!unitSelected) {
                startPoint = e.getPoint();
                selectionRectangle = new Rectangle();
            }
        }


        else if (SwingUtilities.isRightMouseButton(e)) {
            // Odznacz żołnierza
            if (selectedSoldier != null) {
                selectedSoldier.setSelected(false);
                selectedSoldier = null;
            }
            if (selectedMinigunner != null) {
                selectedMinigunner.setSelected(false);
                selectedMinigunner = null;
            }

            if (selectedValkiria != null){
                selectedValkiria.setSelected(false);
                selectedValkiria = null;
            }



            if (selectedBattleVehicle != null) {
                selectedBattleVehicle.setSelected(false);
                selectedBattleVehicle = null;
            }
            // kliknieciem prawym odznaczy zaznaczone jednostki
            if (selectedHarvester != null) {
                selectedHarvester.setSelected(false);
                selectedHarvester = null;
            }

            if (selectedBuilderVehicle != null) {
                cancelBuildingPlacement(); // wyłącza tryb budowy i menu
            }

            if (selectedBaracks != null) {
                selectedBaracks.setSelected(false);
                showBaracksMenu = false;
                updateBaracksMenu();
                selectedBaracks = null;
            }

            if (selectedFactories != null) {
                selectedFactories.setSelected(false);
                showFactorysMenu = false;
                updateFactorysMenu();
                selectedFactories = null;
            }
            if (selectedArtylery != null){
//            selectedArtylery.setTarget(e.getPoint());
                selectedArtylery.setSelected(false);
                showArtysMenu = false;
                updateArtysMenu();
                selectedArtylery = null;

            }
            for (Soldier soldier : soldiers) {
                soldier.setSelected(false);
            }

        }


        if (selectedHarvester != null && SwingUtilities.isLeftMouseButton(e)){
            selectedHarvester.setTarget(e.getPoint());
            selectedHarvester.setSelected(true);
        }
        if (selectedValkiria != null && SwingUtilities.isLeftMouseButton(e)){
            selectedValkiria.setTarget(e.getPoint());
            selectedValkiria.setSelected(true);
        }
        if (selectedBattleVehicle != null && SwingUtilities.isLeftMouseButton(e)){
            selectedBattleVehicle.setTarget(e.getPoint());
            selectedBattleVehicle.setSelected(true);
        }

        if (selectedBuilderVehicle != null && SwingUtilities.isLeftMouseButton(e)) {
            selectedBuilderVehicle.setTarget(e.getPoint());
            selectedBuilderVehicle.setSelected(true);

        }

        // Jeśli wybrano żołnierza, ustaw cel
        if (selectedSoldier != null && SwingUtilities.isLeftMouseButton(e)) {
            selectedSoldier.setTarget(e.getPoint());
            selectedSoldier.setSelected(true);
        }
        if (selectedMinigunner != null && SwingUtilities.isLeftMouseButton(e))
        {
            selectedMinigunner.setTarget(e.getPoint());
            selectedMinigunner.setSelected(true);
        }

        repaint();
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && startPoint != null) {
            // Aktualizowanie prostokąta zaznaczenia podczas przeciągania
            int x = Math.min(startPoint.x, e.getX());
            int y = Math.min(startPoint.y, e.getY());
            int width = Math.abs(startPoint.x - e.getX());
            int height = Math.abs(startPoint.y - e.getY());
            selectionRectangle = new Rectangle(x, y, width, height);

            repaint();
        }
    }

    ////////////// zazanczanie wielu jednostek - na razie ttylko soldier
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && selectionRectangle != null) {
            // Zaznacz żołnierzy
            for (Soldier soldier : soldiers) {
                if (selectionRectangle.intersects(new Rectangle(soldier.getX(), soldier.getY(), 20, 20))) {
                    soldier.setSelected(true);
                    selectedSoldiers.add(soldier);
                }
            }

            for (Valkiria valkiria : valkirias) {
                if (selectionRectangle.intersects(new Rectangle(valkiria.getX(), valkiria.getY(), 20, 20))){
                    valkiria.setSelected(true);
                    selectedValkirias.add(valkiria);
                }
            }

            // Zaznacz BuilderVehicle
            for (BuilderVehicle builder : builderVehicles) {
                if (selectionRectangle.intersects(new Rectangle(builder.getX(), builder.getY(), 20, 20))) {
                    builder.setSelected(true);
                    selectedBuldierVehicles.add(builder);
                }
            }

            selectionRectangle = null;
            startPoint = null;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // Odznacz żołnierzy
            for (Soldier soldier : selectedSoldiers) {
                soldier.setSelected(false);
            }
            selectedSoldiers.clear();
            for (Valkiria valkiria : selectedValkirias){
                valkiria.setSelected(false);
            }
            selectedValkirias.clear();

            // Odznacz BuilderVehicle
            for (BuilderVehicle builder : selectedBuldierVehicles) {
                builder.setSelected(false);
            }
            selectedBuldierVehicles.clear();
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            Point target = e.getPoint();

            for (Soldier soldier : selectedSoldiers) {
                soldier.setTarget(target);
                soldier.setSelected(true);
            }
            for (Valkiria valkiria : selectedValkirias){
                valkiria.setTarget(target);
                valkiria.setSelected(true);
            }

            for (BuilderVehicle builder : selectedBuldierVehicles) {
                builder.setTarget(target);
                builder.setSelected(true);
            }
        }

        repaint();
    }



//    // Obsługuje kliknięcia myszy z terraBionX1
//    private void handleMousePress(MouseEvent e) {
//        if (e.getButton() == MouseEvent.BUTTON1) { // Lewy przycisk myszy
//            int mouseX = e.getX();
//            int mouseY = e.getY();
//
//            // Sprawdzamy, czy kliknięto na żołnierza
//            for (Soldier soldier : soldiers) {
//                if (soldier.contains(mouseX, mouseY)) {
//                    selectedSoldier = soldier;
//                    return;
//                }
//            }
//
//            // Jeśli jest już wybrany żołnierz, ustaw cel ruchu
//            if (selectedSoldier != null) {
//                targetPoint = new Point(mouseX, mouseY);
//            }
//        }
//    }

//    @Override
//    public void mouseReleased(MouseEvent e) {}

//    @Override
//    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}


    @Override
    public void mouseMoved(MouseEvent e) {
        if (isPlacingPowerPlant && selectedBuilderVehicle != null) {
            int x = (e.getX() / BUILD_SIZE) * BUILD_SIZE;
            int y = (e.getY() / BUILD_SIZE) * BUILD_SIZE;

            placementCursor = new Rectangle(e.getX(), e.getY(), BUILD_SIZE, BUILD_SIZE);
            repaint();
        }
    }

//    @Override
//    public void mouseDragged(MouseEvent e) {}

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        // Logika reakcji na zdarzenia
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        // Rysowanie mapy (w tym przypadku tylko prostokąt)


        g.fillRect(0, 0, 3000, 3000); // Rysowanie mapy 3000x3000

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - previousTime;
        previousTime = currentTime;

//        setBackground(Color.WHITE);
        // Rysowanie tła
        if (backgroundImage != null) {
            int imgWidth = backgroundImage.getWidth(null);
            int imgHeight = backgroundImage.getHeight(null);

            for (int x = 0; x < getWidth(); x += imgWidth) {
                for (int y = 0; y < getHeight(); y += imgHeight) {
                    g.drawImage(backgroundImage, x, y, this);
                }
            }
        }
/// ////////// graficzny proces budowy budynkow
        /// ////////
        /// ///////
        /// ///
        /// //
        /// /
        for (BuildingProgress progress : buildingProgressList) {
            if (!progress.isFinished()) {
                // 🔸 Kwadrat pomarańczowy
                g.setColor(new Color(255, 140, 0, 150));
                g.fillRect(progress.x, progress.y, BUILD_SIZE, BUILD_SIZE);

                // 🔸 Pasek postępu
                g.setColor(Color.ORANGE);
                g.drawRect(progress.x, progress.y - 8, BUILD_SIZE, 5);
                int filled = (int) (BUILD_SIZE * progress.getProgress());
                g.fillRect(progress.x, progress.y - 8, filled, 5);
            }
        }

        Iterator<BuildingProgress> iterator = buildingProgressList.iterator();
        while (iterator.hasNext()) {
            BuildingProgress progress = iterator.next();
            if (progress.isFinished()) {
                switch (progress.getType()) {
                    case "PowerPlant":
                        powerPlants.add(new PowerPlant(progress.x, progress.y));
                        totalPower += PowerPlant.getPowerGenerated();
                        break;
                    case "SteelMine":
                        steelMines.add(new SteelMine(progress.x, progress.y));
                        totalPower -= 100;
                        break;
                    case "Baracks":
                        baracks.add(new Baracks(progress.x, progress.y));
                        totalPower -= 100;
                        break;
                    case "ResearchCenter":
                        researchCenters.add(new ResearchCenter(progress.x, progress.y));
                        totalPower -= 200;
                        break;
                    case "ValkiriaTech":
                        valkiriaTechs.add(new ValkiriaTech(progress.x, progress.y));
                        totalPower -= 200;
                        break;
                    case "Factory":
                        factories.add(new Factory(progress.x, progress.y));
                        totalPower -= 150;
                        break;
                    case "Artylery":
                        artylerys.add(new Artylery(progress.x, progress.y));
                        totalPower -= 50;
                        break;
                }
                iterator.remove(); // usuń z listy aktywnych budów
            }
        }



        // Usunięcie wyczerpanych złóż
        resources.removeIf(ResourcesSteel::isDepleted);

        for (ResourcesSteel resource : resources) {
            resource.draw(g);
        }

        for (Baracks b : baracks) {
            b.draw(g);

        }




        for (Artylery artylery : artylerys){
            artylery.draw(g);
            artylery.shoot(g, artBullets, enemies, enemiesToo, hives, enemyShooters);
        }




        if (selectionRectangle != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 255, 50)); // Półprzezroczysty niebieski
            g2d.fill(selectionRectangle);
            g2d.setColor(Color.BLUE); // Krawędź prostokąta
            g2d.draw(selectionRectangle);
        }

        // Rysowanie elektrowni
        for (PowerPlant powerPlant : powerPlants) {
            powerPlant.draw(g);
        }
        for (ValkiriaTech valkiriaTech : valkiriaTechs) {
            valkiriaTech.draw(g);
        }
        for (SteelMine steelMine : steelMines) {
            steelMine.draw(g);
        }


        for (Crystal crystal : crystals){
            crystal.draw(g);
        }
        for (Harvester harvester : harvesters) {
            harvester.draw(g);
        }
        for (ResearchCenter researchCenter : researchCenters)
        {
            researchCenter.draw(g);
        }
        for (Factory factory : factories) {
            factory.draw(g);
            factory.updateValkiriaProduction(valkirias); // <-- NOWE
            factory.spawnBots(g, soldierBots,
                    () -> totalPower,                // Supplier — odczyt
                    () -> totalPower -= 50           // Runnable — zużycie
            ); // Aktualizuje Hive i spawnuje EnemyToo
        }
//        for (Cryopit cryopit : cryopits){
//            cryopit.drawAll(g);
//        }
        for (Flora flora : obstacles) {
            flora.draw(g);
        }
        // Rysowanie żołnierzy
        for (Soldier soldier : soldiers) {
            soldier.draw(g);
            Rectangle viewRect = getVisibleRect();
            soldier.updateFly(deltaTime);

            // Używamy instancji `soldier`, NIE klasy!
            soldier.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    hiveToos,
                    enemyShooters,
                    enemyHunters,

                    enemyBehemoths,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }
        for (Valkiria valkiria : valkirias){
            valkiria.draw(g);
            Rectangle viewRect = getVisibleRect();
            valkiria.updateFly(deltaTime);


            valkiria.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    hiveToos,
                    enemyShooters,
                    enemyHunters,
                    enemyBehemoths,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }

        for (SoldierBot soldierBot : soldierBots){
            soldierBot.draw(g);
            Rectangle viewRect = getVisibleRect();
            soldierBot.updateFly(deltaTime);
            soldierBot.shoot(g,

                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    hiveToos,
                    enemyShooters,
                    enemyHunters,
                    enemyBehemoths,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }



        //budowniczy
        for (BuilderVehicle builderVehicle :builderVehicles) {
            builderVehicle.draw(g);
            builderVehicle.update(deltaTime); //// to do unoszenia i opadania efektu lotu
            Rectangle viewRect = getVisibleRect();

            // Używamy instancji `minigunner`, NIE klasy!
            builderVehicle.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }
        for (Minigunner minigunner : minigunners) {
            minigunner.draw(g);

            Rectangle viewRect = getVisibleRect();

            // Używamy instancji `minigunner`, NIE klasy!
            minigunner.shoot(
                    g,
                    minigunnerBullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }
        // Rysowanie pocisków


        for (BattleVehicle battleVehicle : battleVehicles){
            battleVehicle.draw(g);
            battleVehicle.update(deltaTime);
            Rectangle viewRect = getVisibleRect();

            battleVehicle.shoot(
                    g,
                    bullets,
                    enemies,
                    enemiesToo,
                    hives,
                    enemyShooters,
                    enemyHunters,
                    viewRect.x,
                    viewRect.y,
                    viewRect.width,
                    viewRect.height
            );
        }


        for (Hive hive : hives) {
            hive.draw(g);
            hive.updateActivationAndSpawning(g, soldiers, soldierBots, builderVehicles, enemiesToo, enemyShooters, enemyHunters); // jesdnostki ktore akttywuja tez respa
        }

//        for (Hive hive : hives) {
//            hive.draw(g); // Rysowanie Hive
//        }
// do zwyklego spawnu czasowego
        for (HiveToo hiveToo : hiveToos) {
            hiveToo.draw(g);
            hiveToo.spawnEnemiesToo(g, enemiesToo, enemyShooters, enemyHunters); // Aktualizuje Hive i spawnuje EnemyToo
        }


        for (EnemyHunter enemyHunter : enemyHunters){

            enemyHunter.draw(g);
        }
        for (EnemyBehemoth enemyBehemoth : enemyBehemoths){
            enemyBehemoth.draw(g);
            enemyBehemoth.updateFly(deltaTime);
            enemyBehemoth.shoot(g, projectiles, soldiers, valkirias, soldierBots, battleVehicles, factories, powerPlants, builderVehicles, artylerys, baracks);
        }


        // Rysowanie wrogów
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }

        for (EnemyShooter enemyShooter : enemyShooters){
            enemyShooter.draw(g);
            enemyShooter.shoot(g, projectiles, soldiers, valkirias, soldierBots, battleVehicles, factories, steelMines, powerPlants, builderVehicles, artylerys, baracks);
        }
        //budowniczy
        for (BuilderVehicle builderVehicle :builderVehicles) {
            builderVehicle.draw(g);
        }

        // Rysowanie wrogów EnemyToo
        for (EnemyToo enemyToo : enemiesToo) {
            enemyToo.draw(g);
        }


        for (MinigunnerBullet minigunnerBullet : minigunnerBullets){
            minigunnerBullet.draw(g);
        }
        for (ArtBullet artBullet : artBullets) {
            artBullet.draw(g);
        }

        for (int i = explosions.size() - 1; i >= 0; i--) {
            Explosion explosion = explosions.get(i);
            explosion.draw(g);

            if (explosion.isExpired()) {
                explosions.remove(i);
            }
        }

        // Rysowanie pocisków
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }


        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for (int i = 0; i < hitFlashes.size(); i++) {
            HitFlash flash = hitFlashes.get(i);
            flash.draw(g);
            if (flash.isExpired()) {
                hitFlashes.remove(i);
                i--;
            }
        }


        // Rysowanie prostokąta zaznaczenia
        if (selectionRectangle != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 255, 50)); // Półprzezroczysty niebieski
            g2d.fill(selectionRectangle);
            g2d.setColor(Color.BLUE); // Krawędź prostokąta
            g2d.draw(selectionRectangle);
        }
        //to jest od stawiania budynkow czerwone/biale pole
        /// ///////////////////
        /// ///////////////
        /// /////////
        /// ////////
        /// /////////
        if (isPlacingBuilding && placementCursor != null && selectedBuilderVehicle != null) {
            Graphics2D g2 = (Graphics2D) g;

            int builderX = selectedBuilderVehicle.getX() + 25;
            int builderY = selectedBuilderVehicle.getY() + 25;
            int cursorCenterX = placementCursor.x + BUILD_SIZE / 2;
            int cursorCenterY = placementCursor.y + BUILD_SIZE / 2;

            double distance = Point.distance(builderX, builderY, cursorCenterX, cursorCenterY);

            boolean tooCloseToFactory = false;
            boolean collisionDetected = false;
            boolean tooFarFromCrystal = false; // 🔹 nowy warunek

            // 🔹 Sprawdzenie odległości od innych fabryk (jeśli stawiamy fabrykę)
            if ("Factory".equals(placingBuildingType)) {
                for (Factory factory : factories) {
                    int factoryCenterX = factory.getX() + factory.getWidth() / 2;
                    int factoryCenterY = factory.getY() + factory.getHeight() / 2;

                    double factoryDistance = Point.distance(factoryCenterX, factoryCenterY, cursorCenterX, cursorCenterY);
                    if (factoryDistance < 350) {
                        tooCloseToFactory = true;
                        break;
                    }
                }
            }

            // 🔹 Jeśli stawiamy ValkiriaTech — sprawdź, czy jest w zasięgu Crystala


            if (placingBuildingType != null && placingBuildingType.toLowerCase().contains("valkiria")) {
                boolean insideCrystalArea = false;
                Rectangle newBuilding = new Rectangle(placementCursor.x, placementCursor.y, BUILD_SIZE, BUILD_SIZE);

                for (Crystal crystal : crystals) {
                    if (crystal.getBuildArea(180).contains(newBuilding)) {
                        insideCrystalArea = true;
                        break;
                    }
                }

                if (!insideCrystalArea) {
                    tooFarFromCrystal = true;
                    // 🔹 debug info

                }
            }




            Rectangle newBuilding = new Rectangle(placementCursor.x, placementCursor.y, BUILD_SIZE, BUILD_SIZE);

            // 🔹 Sprawdź kolizję z istniejącymi budynkami
            for (PowerPlant plant : powerPlants)
                if (plant.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (SteelMine mine : steelMines)
                if (mine.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (ResearchCenter researchCenter : researchCenters)
                if (researchCenter.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (ValkiriaTech valkiriaTech : valkiriaTechs)
                if (valkiriaTech.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (Crystal crystal : crystals)
                if (crystal.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (Baracks barack : baracks)
                if (barack.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (Artylery artylery : artylerys)
                if (artylery.getBounds().intersects(newBuilding)) collisionDetected = true;
            for (Factory factory : factories)
                if (factory.getBounds().intersects(newBuilding)) collisionDetected = true;

            // 🔹 Sprawdź kolizję z przeszkodami (Flora)
            for (Flora obstacle : obstacles) {
                if (obstacle.getCollisionBounds().intersects(newBuilding)) {
                    collisionDetected = true;
                    break;
                }
            }

// 🔹 Ustal kolor kwadratu budowy
            if (distance <= BUILD_RANGE && !tooCloseToFactory && !collisionDetected && !tooFarFromCrystal) {
                g2.setColor(Color.WHITE);
            } else {
                g2.setColor(Color.RED);
            }

            // 🔹 Narysuj kwadrat budowy
            g2.drawRect(placementCursor.x, placementCursor.y, BUILD_SIZE, BUILD_SIZE);
        }
/// ////// postep budowania
        for (BuildingProgress bp : buildingProgressList) {
            double progress = bp.getProgress();

            g.setColor(new Color(255, 140, 0, 120)); // pomarańczowe półprzezroczyste tło
            g.fillRect(bp.x, bp.y, BUILD_SIZE, BUILD_SIZE);

            g.setColor(Color.ORANGE);
            g.drawRect(bp.x, bp.y, BUILD_SIZE, BUILD_SIZE);

//            // pasek postępu (zielony)
//            int barWidth = (int)(BUILD_SIZE * progress);
//            g.setColor(Color.GREEN);
//            g.fillRect(bp.x, bp.y + BUILD_SIZE + 4, barWidth, 5);
//
//            g.setColor(Color.BLACK);
//            g.drawRect(bp.x, bp.y + BUILD_SIZE + 4, BUILD_SIZE, 5);
        }


// Wyświetlanie zasobów niezależnie od przewijania mapy
        Graphics2D g2dOverlay = (Graphics2D) g;
        Font largeFont = new Font("Arial", Font.BOLD, 15); // Czcionka Arial, pogrubiona, rozmiar 15
        g2dOverlay.setFont(largeFont);
        g2dOverlay.setColor(Color.WHITE);

// Oblicz przesunięcie widoku z JScrollPane (czyli przesunięcie mapy)
//        if (getParent() instanceof JViewport viewport) {
//            Point viewPos = viewport.getViewPosition();
//            int screenX = viewPos.x;
//            int screenY = viewPos.y;
//
//            // Tekst zawsze przyklejony do lewego górnego rogu widoku
//            g2dOverlay.drawString("Steel Collected: " + collectedSteel, screenX + 20, screenY + 30);
//            g2dOverlay.drawString("Power: " + totalPower, screenX + 20, screenY + 60);
//        }
        if (getParent() instanceof JViewport viewport) {
            Point viewPos = viewport.getViewPosition();
            int screenX = viewPos.x;
            int screenY = viewPos.y;
/// / tu amsz przyciski by poruszaly sie razem z ekranem
            btnPowerPlant.setLocation(screenX + 10, screenY + 90);
            btnSteelMine.setLocation(screenX + 10, screenY + 130);
            btnBaracks.setLocation(screenX + 10, screenY + 170);
            btnFactory.setLocation(screenX + 10, screenY + 210);
            btnArtylery2.setLocation(screenX + 10, screenY + 250);
            btnResearch.setLocation(screenX + 10, screenY + 290);
            btnValkiriaTech.setLocation(screenX + 10, screenY + 330);

            btnSoldier.setLocation(screenX + 10, screenY + 90);
            btnProduceShell.setLocation(screenX + 10, screenY + 130);
            btnFireShell.setLocation(screenX + 10, screenY + 170);

            // Dodaj te:
            btnHarvester.setLocation(screenX + 10, screenY + 90);
            btnArtylery.setLocation(screenX + 10, screenY + 130);
            btnBattleVehicle.setLocation(screenX + 10, screenY + 170);
            btnBuilderVehicle.setLocation(screenX + 10, screenY + 210);
            btnDroneBot.setLocation(screenX + 10, screenY + 290);
            btnValkiria.setLocation(screenX + 10, screenY + 330);
            btnDestructionFactory.setLocation(screenX + 10, screenY + 380);

            btnDestructionArty.setLocation(screenX + 10, screenY + 90);

            btnBombardment.setLocation(screenX + 1680, screenY + 380);

        }
        if (miniMapPanel != null) {
            miniMapPanel.repaint();
        }
        if (missionFailed) {
            Graphics2D g2d = (Graphics2D) g.create(); // kopiujemy Graphics, żeby nie psuć oryginalnego
            g2d.setColor(new Color(0, 0, 0, 180)); // półprzezroczyste tło

            int overlayWidth, overlayHeight;
            int textX, textY;

            // Pobranie widoku gracza (JViewport)
            if (getParent() instanceof JViewport viewport) {
                Point viewPos = viewport.getViewPosition();
                overlayWidth = viewport.getWidth();
                overlayHeight = viewport.getHeight();

                g2d.fillRect(viewPos.x, viewPos.y, overlayWidth, overlayHeight);

                String text = "MISSION FAILED";
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                int textWidth = g2d.getFontMetrics().stringWidth(text);
                int textHeight = g2d.getFontMetrics().getHeight();

                textX = viewPos.x + (overlayWidth - textWidth) / 2;
                textY = viewPos.y + (overlayHeight - textHeight) / 2 + g2d.getFontMetrics().getAscent();

                g2d.drawString(text, textX, textY);
            }
            g2d.dispose(); // zwolnienie kopii Graphics
        }

        // /// to do FPS by bylo
        frames++;

        if (System.currentTimeMillis() - fpsTimer >= 1000) {
            fps = frames;
            frames = 0;
            fpsTimer += 1000;
        }

    }
}