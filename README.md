# RealTimeSync
現実世界とゲーム内時間の同期が行えます。  
コンソールからの実行、ゲーム内での実行に関わらず、全てのワールドで同期のオンオフが切り替わります。  
[Download](https://forum.mcbe.jp/resources/541/download)  

### コマンド
全てのコマンドはOP権限が必要です。  
| コマンド | 説明 |
|:--:|:---|
|`/rton`|	現実時間とゲーム内時間の同期をオンにします。 |
|`/rtoff`|	現実時間とゲーム内時間の同期をオフにします。 |

### Config
同期設定はコマンドとConfig両方で行うことができます。  
```YAML
'#設定。trueもしくはfalse。': ''
'#設定の初期化を行いたい場合、ymlごと削除を推奨します。': ''
'#現実時間同期': ''
rt: true
'#デバッグモード。通常時はfalse推奨。': ''
debugmode: false
```
### 詳細
3.6秒おきに時刻を更新します。 

### アップデート予定
今後、各ワールドで同期の切り替えが行えるようにする予定です。  

### ライセンス
製作者偽りと二次配布、改造配布、プラグインの横流し、悪用を禁止します。  
自分用、又は友人用の改造のみ問題ありません。  
このプラグインで発生したすべての問題に対して製作者は責任は負いません。  
都合により、プラグインの破棄を要請する場合があります。その際は、要請に従いプラグインを破棄してください。  
