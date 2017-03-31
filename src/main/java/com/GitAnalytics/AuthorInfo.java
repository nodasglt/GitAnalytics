/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.GitAnalytics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

/**
 *
 * @author nodas
 */
public class AuthorInfo
{
    private final LinkedList<CommitInfo> mCommits;
    private final Map<Ref, Integer> mCommitsPerBranch;

    private AuthorInfo()
    {
        mCommits = new LinkedList<>();
        mCommitsPerBranch = new HashMap<>();
    }

    public AuthorInfo incrementCommitCounterOnBranch(BranchInfo branch)
    {
        int count = 1;

        if (mCommitsPerBranch.containsKey(branch.getBranchRef()))
        {
            count = mCommitsPerBranch.get(branch.getBranchRef()) + 1;               
        }

        mCommitsPerBranch.put(branch.getBranchRef(), count);
        
        return this;
    }
    
    public AuthorInfo addCommit(CommitInfo commit) throws Exception
    {
        mCommits.add(commit);
        
        return this;
    }

    public int getTotalCommits()
    {
        return mCommits.size();
    }
    
    public LinkedList<CommitInfo> getCommits()
    {
        return mCommits;
    }

    public int getTotalCommitsOnBranch(BranchInfo branch)
    {
        if (mCommitsPerBranch.containsKey(branch.getBranchRef()))
        {
            return mCommitsPerBranch.get(branch.getBranchRef());               
        }

        return 0;
    }
    
    public static Map<String, AuthorInfo> getAllAuthors(Git git, List<BranchInfo> branches, List<CommitInfo> commits) throws Exception
    {
        Map<String, AuthorInfo> authors = new HashMap<>();
        
        
        for (CommitInfo commit : commits)
        {
            String name = commit.getAuthorName();
            if (authors.containsKey(name))
            {
                authors.get(name).addCommit(commit);
            }
            else
            {
                authors.put(name, new AuthorInfo().addCommit(commit));
            }
        }
        
        branches.forEach((BranchInfo branch) ->
        {
            branch.getCommits().forEach((CommitInfo commit) ->
            {
                authors.get(commit.getAuthorName()).incrementCommitCounterOnBranch(branch);
            });
        });
        
        return authors;
    }
};
