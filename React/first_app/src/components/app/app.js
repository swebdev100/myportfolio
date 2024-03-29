import React, {Component} from 'react';
import AppHeader from '../app-header';
import SearchPanel from '../search-panel/search-panel';
import PostStatusFilter from '../post-status-filter/post-status-filter';
import PostList from '../post-list/post-list';
import PostAddForm from '../post-add-form/post-add-form';

import './app.css';
import styled from 'styled-components';

const AppBlock = styled.div`
    margin: 0 auto;
    max-width: 800px;
`;

// const StyledAppBlock = styled(AppBlock)`
//     background-color: grey;
    
// `

export default class App extends Component {
  constructor(props){
      super(props);
      this.state = {
        data : [
          {label: 'GGGGGGG', important: false, like: false, id: 1},
          {label: 'PPPPPPP', important: true, like: false, id: 2},
          {label: 'YYYYYYY', important: false, like: false, id: 3}
        ], 

        term: ''
      }

      this.deleteItem = this.deleteItem.bind(this);
      this.addItem = this.addItem.bind(this);
      this.onToggleImportant = this.onToggleImportant.bind(this);
      this.onToggleLiked = this.onToggleLiked.bind(this);
      this.onUpdateSearch = this.onUpdateSearch.bind(this);
      
      this.maxId = 4;
  }
  
  deleteItem(id){
     this.setState(({data}) => {
        const index = data.findIndex(elem => elem.id === id);
        const newArr = [...data.slice(0, index), ...data.slice(index + 1)]

        return{
          data: newArr
        }
     });
  }

  addItem(body){
    const newItem = {
      label: body,
      important: false,
      id: this.maxId++
    }

    this.setState(({data}) =>{
      const newArr = [...data, newItem];
      return {
        data: newArr
      }
    })
  }

  onToggleImportant(id){
    this.setState(({data}) =>{
      const index = data.findIndex(elem => elem.id === id);
      const old = data[index];
      const newItem = {...old, important: !old.important}

      const newArr = [...data.slice(0, index), newItem, ...data.slice(index + 1)];

      return{
        data: newArr
      }

  })

  }

  onToggleLiked(id){
    this.setState(({data}) =>{
        const index = data.findIndex(elem => elem.id === id);
        const old = data[index];
        const newItem = {...old, like: !old.like}

        const newArr = [...data.slice(0, index), newItem, ...data.slice(index + 1)];

        return{
          data: newArr
        }

    })
  }

  searchPost(items, term){

    if(term.length === 0){
      return items
    }

    items.filter( (item) => {
      return item.label.indexOf(term) > -1
    });

  }


  onUpdateSearch(term){
    this.setState({term})

  }
 
    render(){
      const {data, term} =this.state;
      const liked = data.filter(item => item.like).length;
      const allPosts = data.length;
      const visiblePosts = this.searchPost(data, term);

      return(
        <div className="app">
          <AppHeader
            liked = {liked}
            allPosts = {allPosts}
          />
          <div className="search-panel d-flex">
            <SearchPanel
             onUpdateSearch = {this.onUpdateSearch}/>
            <PostStatusFilter/>        
          </div>

          <PostList 
            posts = {visiblePosts}
            onDelete = {this.deleteItem}
            onToggleImportant = {this.onToggleImportant}
            onToggleLiked = {this.onToggleLiked}
          />
          <PostAddForm
            onAdd = {this.addItem}
          />
        </div>
      )
    }  
}

