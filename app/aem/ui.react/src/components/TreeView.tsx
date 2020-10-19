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
  isValid: boolean
};

export type Transition = {
  from: TreeNode,
  to: TreeNode,
  cycleDetected: boolean,
  transitionType: string
}

type Graph = {
  nodes: TreeNode[],
  transitions: Transition[]
}

export const fetchData = async (): Promise<Graph | null> => {
  const data = await axios.get('/bin/apm/graph');
  if (data.status === 200) {
    return data.data;
  }
  return null;
};

export const TreeView = () => {
  const [graphData, setData] = useState<Graph | null>(null);
  const [nodes, setNodes] = useState<TreeNode[] | undefined>([])

  useEffect(() => {
    fetchData()
    .then((data) => {
      console.log(data);
      setData(data);
      setNodes(data?.nodes)
    })
    .then(() =>
        console.log(nodes))
  }, []);


  const markup = nodes && nodes.map((item, key) =>
      <div>{item.script.name}</div>
  );/* items && items.map((item, key) =>
      <TreeNodeComponent key={key} script={item.script} children={item.children}/>);
*/
  return <>{markup}</>
};