/**
 * @description: indexDB 在使用的时候，直接生产库，然后查看是否有表，有表添加数据或者修改数据，没有表就进行新增
 * @param databaseName => 数据库名字
 * @param table => 数据库表名字
 * @param key => 存储时候的键（indexDB是根据键值进行查询）
 * @param data => 进行新增的数据或者说是修改的数据，具体看调用方法
 * @param callback => 回调函数（indexDB是异步的，所以大部分都用回调或者Promise去执行）
 * @return {type} 
 */

 if (!window.localStorage.getItem('version')) {
  window.localStorage.setItem('version', 1)
}
let dexie = {
  init: (databaseName, table, flags, callback) => {
      let flag = flags;
      let verIndex = window.localStorage.getItem('version')
      var request = window.indexedDB.open(databaseName, verIndex);
      request.onerror = () => {
          let errdata = {
              status: 'error',
              mes: '打开数据库失败！'
          }
          callback(errdata)
      };
      var indexdb;
      request.onsuccess = (event) => {
          indexdb = event.target.result;
          if (flag == true) {
              verIndex = parseInt(window.localStorage.getItem('version')) + 1
              window.localStorage.setItem('version', verIndex);
          } else {
              verIndex = parseInt(window.localStorage.getItem('version'))
          }
          if (indexdb.objectStoreNames.length > 0) {
              flag = true
          }
          indexdb.isCreate = flag;
          flag = false;
          callback(indexdb)
      };
      request.onupgradeneeded = (event) => {
          indexdb = event.target.result;
          if (!indexdb.objectStoreNames.contains(table)) {
              if (flag) {
                  indexdb.createObjectStore(table, {
                      keyPath: 'id'
                  });
              }
          }
      }
  },
  insertData: (databaseName, table, key, data, callback) => {
      dexie.init(databaseName, table, true, (db) => {
          try {
              var addData = [{
                  id: key,
                  value: data
              }]
              var transaction = db.transaction(table, 'readwrite');
              var store = transaction.objectStore(table);
              for (var i = 0; i < addData.length; i++) {
                  store.add(addData[i]);
              }
              let backData = {
                  status: 'success',
                  dataBase: databaseName,
                  table: table,
                  data: addData
              }
              callback(backData)
          } catch (error) {
              let errdata = {
                  status: 'error',
                  mes: '数据写入失败！'
              }
              callback(errdata)
          }
          db.close()
      })
  },
  selectData: (databaseName, table, key, callback) => {
      dexie.init(databaseName, table, false, (db) => {
          if (db.isCreate == false) {
              let errdata = {
                  status: 'error',
                  mes: '获取数据失败！'
              }
              callback(errdata)
              db.close()
              return
          } else {
              var transaction = db.transaction(table, 'readwrite');
              var store = transaction.objectStore(table);
              var request = store.get(key);
              request.onsuccess = (e) => {
                  try {
                      var resultData = e.target.result;
                      let backData = {
                          status: 'success',
                          dataBase: databaseName,
                          table: table,
                          data: resultData.value
                      }
                      callback(backData)
                  } catch (error) {
                      let errdata = {
                          status: 'error',
                          mes: '数据读取失败！数据库中没有该数据！'
                      }
                      callback(errdata)
                  }
              };
              request.onerror = () => {
                  let errdata = {
                      status: 'error',
                      mes: '数据读取失败！'
                  }
                  callback(errdata)
              }
          }
          db.close()
      })
  },
  deleteData: (databaseName, table, key, callback) => {
      dexie.init(databaseName, table, false, (db) => {
          if (db.isCreate == false) {
              let errdata = {
                  status: 'error',
                  mes: '删除数据失败！'
              }
              callback(errdata)
              db.close()
              return
          } else {
              var transaction = db.transaction(table, 'readwrite');
              var store = transaction.objectStore(table);

              var getresult = store.get(key);
              let pro = new Promise((resolved) => {
                  getresult.onsuccess = (e) => {
                      resolved(e.target.result)
                      db.close()
                  }
              })
              pro.then(res => {
                  var result = store.delete(key);
                  if (res != undefined) {
                      result.onsuccess = () => {
                          let errdata = {
                              status: 'success',
                              mes: '删除成功！',
                          }
                          callback(errdata)
                      }
                      result.onerror = () => {
                          let errdata = {
                              status: 'error',
                              mes: '删除失败！'
                          }
                          callback(errdata)
                      }
                  } else {
                      let errdata = {
                          status: 'error',
                          mes: '数据库中已经没有该数据！'
                      }
                      callback(errdata)
                  }
                  db.close()
              })
          }
      })
  },
  changeData: (databaseName, table, key, data, callback) => {
      dexie.init(databaseName, table, true, (db) => {
          try {
              var addData = {
                  id: key,
                  value: data
              }
              var transaction = db.transaction(table, 'readwrite');
              var store = transaction.objectStore(table);
              store.put(addData);
              let backData = {
                  status: 'success',
                  dataBase: databaseName,
                  table: table,
                  data: addData
              }
              callback(backData)
          } catch (error) {
              let errdata = {
                  status: 'error',
                  mes: '更新写入失败！'
              }
              callback(errdata)
          }
          db.close()
      })
  },
  clearTable: (databaseName, table, callback) => {
      dexie.init(databaseName, table, true, (db) => {
          var transaction = db.transaction(table, 'readwrite');
          var store = transaction.objectStore(table);
          var result = store.clear();
          result.onsuccess =  ()=> {
              let successmsg = {
                  status: "success",
                  msg: "数据表" + table + "清空成功！",
              }
              callback(successmsg)
          }
          result.onerror = () => {
              let successmsg = {
                  status: "success",
                  msg: "数据表" + table + "清空失败！",
              }
              callback(successmsg)
          }
      })
  },
  dexie: () => {

  }
}

export default dexie

//调用方法，我是挂载到vue上面去使用的
// this.$indexDB.insertData("indexDBname","tablename","indexkey", {name: "lxl"},(res) => {console.log(res);}); //插入数据-增
// this.$indexDB.deleteData("indexDBname","tablename","indexkey", (res) => {console.log(res);}); //删除数据-删
// this.$indexDB.changeData("indexDBname","tablename","indexkey",{ name: "lxl" },(res) => {console.log(res);}); //修改数据-改
// this.$indexDB.selectData("indexDBname","tablename","indexkey", (res) => { console.log(res);}); //获取数据-查
// this.$indexDB.clearTable("indexDBname","tablename", (res) => {console.log(res);}); //清空库表，不是清库，是清表数据
