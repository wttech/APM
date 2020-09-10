import React, {useEffect, useState} from 'react';
import axios from "axios";
import TreeNodeComponent from './TreeNodeComponent';

export type Script = {
  name: string,
  path: string,
  valid: boolean
};

export type TreeNode = {
  script: Script,
  children: TreeNode[]
};


export const fetchData = async (): Promise<TreeNode[]> => {
  const data = await axios.get('/bin/apm/graph');
  if (data.status === 200) {
    return data.data;
  }
  return [];
};

export const TreeView = () => {
  const [items, setItems] = useState<TreeNode[]>([]);


    useEffect(() => {
      fetchData().then((data) => setItems(data));
    }, []);


  const markup = items && items.map((item, key) =>
      <TreeNodeComponent key={key} script={item.script} children={item.children}/>);

  return <>{markup}</>
};