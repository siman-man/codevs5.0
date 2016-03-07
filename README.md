## codevs5.0
codevs5.0


## 実装するものリスト

### Ready

- [ ] どの岩を破壊するのが最適かを求める
- [ ] このターンに岩を置かれると負ける場所を調べる
- [ ] このターン自分が何もしない場合にまけるかどうかを調べる
- [ ] 忍術の優先度を決める
- [ ] 忍犬から逃げるモードを作る
- [ ] 忍犬の集団から一番遠い場所を見つける

### Doing

- [ ] 各ニンジャソウルへの目的地へのコストをもう少し良くする
- [ ] 相手の嫌なところに岩を落とす

### Done

- [x] 評価時に犬を移動させるように
- [x] 今のフィールドの状態から評価値を出す
- [x] 犬の位置を更新処理を作る (バグってるのでやり直し)
- [x] 入力の読み込み
- [x] 壁判定
- [x] 指定した座標から指定した方向に移動が可能かどうかを確かめる
- [x] フィールドの各要素をセルとして定義する
- [x] フィールドの危険度マップを作成する
- [x] 忍者を移動させる処理
- [x] 忍者をはじめの位置に戻す処理
- [x] 移動パターンを全て列挙する
- [x] フィールドのロールバック機能
- [x] 石を押す処理を入れる(移動のメソッドに組み込み？）
- [x] 任意の2点間の距離を求める(ワーシャルフロイドで事前に全部探索しておく) (要改良)
- [x] ニンジャソウルに対して、2人の忍者がそれぞれ別々のものを狙うようにマッピングする
- [x] 忍犬の危険度を設定する
- [x] 各移動について評価をつける
- [x] 行動した際の情報をまとめたクラスを作成 (ActionInfo)
- [x] 行動リストの中で一番評価が高いやつを出力する
- [x] 決定した行動を出力する
- [x] 移動が有効かどうかを確認する
- [x] 忍者がそのターンで移動出来る中で一番最大の評価を計算する


## フィールドについて

* 縦幅 17
* 横幅 14
* 初期配置は相手と一緒


## 移動について

* 忍犬はすり抜けられる(移動先に居なければ途中の経路について捕獲判定は発生しない)
* 移動毎にニンジャソウルの取得判定が発生する
* 移動できないマスに対して移動を行おうとした場合にはその場にとどまる。


## 考察

* ニンジャソウルを取得することで相手の陣地に忍犬を出現させることが出来る。
* 初期状態では恐らく忍犬はいない
* 最初はニンジャソウルを効率よく取得するための経路を作成する必要がある。
* ニンジャソウルを効率的に集め、相手の陣地に忍犬を送りまくる
* 相手より常に多いニンジャソウルを維持することで、相手の攻撃に対応出来る

* コストの大きさが「相手落石 < 自雷撃」 の場合、相手が落とした岩を破壊するたびに差分のコスト損してる


## ニンジャソウルの収集アルゴリズムについて

* 一番近いニンジャソウルを取得していく (幅優先探索)
* 忍犬をよけつつ経路を作成(A*)
* 全部のニンジャソウルを考慮した経路作成 (リーマン問題)、実際には取得した瞬間に新しいソウルが発生するので、あまり意味ないかも


## 岩について

* 岩の移動先が「岩、忍犬、忍者、壁」ではない場合のみに押すことが出来る。（床と忍者ソウルのみ、分身も可）
* 角に設置すると破壊以外で動かない
* 出来るだけ動かない岩の数を少なくしないと厳しい


### 移動がどれだけ制限されるのか

* 岩が4つ固まると破壊以外でもうこの岩が動くことはないので強力


## 忍犬について

* 忍者への最短経路長が短い順に行動、等しい場合はidが若いやつから行動
* 忍者への到達経路が存在しない場合はその場にとどまる。


### 召喚場所について

* 各忍者から一番遠いところに召喚される


## 忍術について

* 1ターンに1回だけ使用可能
* 不発でもコストを消費する (相手の術を先読みして発動させた術が失敗する可能性)
* コスト1のときの超高速がかなり強い(出来れば積極的に使っていきたい)
* 岩破壊が今のところ緊急脱出以外で使用が無い
* 移動時は「上、左、右、下」の順で行動を行う



### 超高速 (コスト: 1 - 8)

1ターンの間に3回行動できるようになる

### 自落石 (コスト: 3 - 7)

* 自分のフィールドに岩を1個設置出来る
* 「忍者、忍犬、ニンジャソウル、岩、壁」がある座標には岩を設置出来ない

自陣に引きこもりたい時とか、忍犬の進路を妨害したいときに使うかも
ただ、コストと効果が見合ってない気がする


### 敵落石 (コスト: 3 - 7)

* 相手のフィールドに岩を1個設置出来る
* 「忍者、忍犬、ニンジャソウル、岩、壁」がある座標には岩を設置出来ない

相手の妨害が可能、発動のタイミングによっては相手を倒すことが出来る


### 自雷撃 (コスト: 3 - 7)

自分のフィールドの岩を1個破壊


### 敵雷撃 (コスト: 1 - 5)

敵のフィールドの岩を1個破壊

正直メリット無い


### 自分身 (コスト: 2 - 4)

* 自分のフィールドに分身を1個設置
* 指定座標が岩や壁の場合は設置に失敗


### 敵分身

* 相手のフィールドに分身を1個設置


### 回転斬り (6 - 30)

* 忍者1人の周囲8マスにいる忍犬を相手のフィールドに転送します

リーサル・ウェポン


## 戦略について

### 序盤 (試合開始直後)

* ニンジャソウルをひたすら集める
* 可能であれば高速移動を多用 （もしかしたら微妙かも）
* 岩が多い場合は破壊優先、一時的にソウルを集めたところで岩の中に発生したソウルを収集出来なくなるため


### 中盤 (忍犬が互いのフィールドにある程度存在してきた)

* 常にニンジャソウルに対して最短路で取得できるように頑張る
* 岩は破壊、余裕が出てきたら相手のフィールドに岩を送り込む

## 終盤 (お互いつらい)