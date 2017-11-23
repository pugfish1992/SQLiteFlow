SQLiteFlow - Quick Start Guide
========

### 1. Table

最もシンプルなテーブルの定義は次のようになります.

```
@Table("dinosaur")
class Schema {

  @PrimaryKey
  @Column("id")
  public long id;
}
```

`@Table` は、付与されたクラスがテーブルの定義クラスであることを示します. 引数にはテーブル名を指定します. `@Column` は、付与されたフィールドがテーブルの列であることを示します. 引数には列名を指定します. 各テーブルは必ず１つの `long` 型で定義された PrimaryKey フィールドを持つ必要があります. PrimaryKey フィールドを指定するには, `@PrimaryKey` を使用します.

### 2. Columns

PrimaryKey の他にいくつかの列を定義すると、次のようになります.

```
@Table("dinosaur")
class Schema {

  @PrimaryKey
  @Column("id")
  public long id;

  @Column("name")
  public String name;

  @Column("period")
  public int period;

  @DefaultBool(false)
  @Column("is_liked")
  public boolean isLiked;
}

```

利用できる型は `short`, `int`, `long`, `float`, `double`, `boolean`, `String` の７つです. `@DefaultXXX` で初期値を指定できます. 
`boolean` 型には `@DefaultBoolean`, `int` 型には `@DefaultInt` など、それぞれの型に対して適切なものを使用する必要があります.

### 3. Code Generation

プロジェクトをビルドすると,　次のような Entry クラスと Table クラスが生成されます(概要).

```
// Entry Class

public class Dinosaur extends Entry {
 
  public long id;
  public String name;
  public int period;
  public boolean isLiked = false;
  
  // Required public default constructor
  public Dinosaur() {}
  public boolean save() {}
  public boolean save(AbsValidator.ValidationErrorListener l) {}
  public boolean delete() {}
}
```

```
// Table Class

public final class DinosaurTable extends Table {

  public static final Column id = new Column("id", "dinosaur", AffinityType.INTEGER, true);
  public static final Column name = new Column("name", "dinosaur", AffinityType.TEXT, false);
  public static final Column period = new Column("period", "dinosaur", AffinityType.INTEGER, false);
  public static final Column isLiked = new Column("is_liked", "dinosaur", AffinityType.INTEGER, false);

  // Required public default constructor  
  public DinosaurTable() {}
  public String getName() {}
  public Set<Column> getColumnSet() {}
  public Column getPrimaryKeyColumn() {}
}
```

### 4. Create a SQLiteOpenHelper

Android でSQLite に接続する際に必要な, `SQLiteOpenHelper` を継承したクラスを作成します. `AbsSQLiteOpenHelper` クラスを継承すれば、テーブルを作成するための Utility メソッドを利用できます.

```
public class DbOpenHelper extends AbsSQLiteOpenHelper {

  private static final String DATABASE_NAME = "SQLiteFlowSample.db";
  private static final int DATABASE_VERSION = 1;

  public DbOpenHelper(Context context) {
    super(context, DATABASE_NAME, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Utility method to create a table using a Table class
    super.createTable(DinosaurTable.class, db);
  }
}
```

### 5. Initialize SQLiteFlow

自動生成されたクラスがデータベースに接続できるように, アプリの起動時に１度だけ `SQLiteFlow` を初期化する必要があります. ここでは, `Application` クラスを継承たクラスを作成し、 `onCreate()` メソッド内で初期化しています.

```
public class MyApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    SQLiteFlow.init(new DbOpenHelper(this));
  }
}
```

また、作成した `MyApp` クラスが自動的にインスタンス化されるように `AndroidManifest.xml` に以下を追加します.

```
  <application  
    ...
    android:name=".MyApp">    
      ...
  </application>

```

### 6. Use generated classes in your code

次のようにしてデータの保存,削除,検索ができます.

```
// An enum definition
enum Period {
  CRETACEOUS(0), // 145.5 million years ago — 66.0 million years ago.
  JURASSIC(1),  // 201.3 million years ago — 145.0 million years ago.
  TRIASSIC(2);  // 252.17 million years ago — 201.3 million years ago.

  private final mId;
  
  Period(int id) { mId = id; }
  public int getId() { return mId; }
}


// Save data
Dinosaur dinosaur = new Dinosaur();
dinosaur.name = "tyrannosaurus";
dinosaur.period = Period.JURASSIC.getId();
dinosaur.save();


// Delete data
dinosaur.delete();


// Search data in the database
List<Dinosaur> dinosaurs = Select
                .target(Dinosaur.class)
                .from(DinosaurTable.class)
                .where(DinosaurTable.period.equalsTo(Period.JURASSIC.getId()))
                .start();

```

