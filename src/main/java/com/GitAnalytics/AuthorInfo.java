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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 *
 * @author nodas
 */
public class AuthorInfo
{
    private final LinkedList<CommitInfo> mCommits;
    private Map<Ref, Integer> mCommitsPerBranch;

    private AuthorInfo(List<Ref> branches)
    {
        mCommits = new LinkedList<>();
        mCommitsPerBranch = new HashMap<>();

        branches.forEach((branch) -> {
            mCommitsPerBranch.put(branch, 0);
        });
    }

    public AuthorInfo incrementCommitCounterOnBranch(Ref branch)
    {
        int count = 1;

        if (mCommitsPerBranch.containsKey(branch))
        {
            count = mCommitsPerBranch.get(branch) + 1;               
        }

        mCommitsPerBranch.put(branch, count);
        
        return this;
    }
    
    public AuthorInfo addCommit(Repository repo, RevCommit commit)
    {
        mCommits.add(new CommitInfo(repo, commit, new LinkedList<>()));
        
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

    public int getTotalCommitsOnBranch(Ref branch)
    {
        if (mCommitsPerBranch.containsKey(branch))
        {
            return mCommitsPerBranch.get(branch);               
        }

        return 0;
    }
    
    public static Map<String, AuthorInfo> getAllAuthors(Repository repo, Git git) throws Exception
    {
        Map<String, AuthorInfo> authors = new HashMap<>();
        List<Ref> branches = git.branchList().call();
        
        for (RevCommit commit : git.log().all().call())
        {
            String name = commit.getAuthorIdent().getName();
            if (authors.containsKey(name))
            {
                authors.get(name).addCommit(repo, commit);
            }
            else
            {
                authors.put(name, new AuthorInfo(branches).addCommit(repo, commit));
            }
        }
        
        for (Ref branch : branches)
        {
            for (RevCommit commit : git.log().add(branch.getObjectId()).call())
            {
                authors.get(commit.getAuthorIdent().getName()).incrementCommitCounterOnBranch(branch);
            }
        }
        
        return authors;
    }
};
