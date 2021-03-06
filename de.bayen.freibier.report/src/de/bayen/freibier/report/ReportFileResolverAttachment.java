package de.bayen.freibier.report;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import net.sf.jasperreports.engine.util.FileResolver;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAttachment;
import org.compiere.model.MAttachmentEntry;
import org.compiere.model.PO;

public class ReportFileResolverAttachment extends ReportFileResolver {

	private PO record;
	
	public ReportFileResolverAttachment(FileResolver parent, PO record) {
		super(parent);
		this.record=record;
	}
	
	@Override
	protected Boolean checkCacheFreshness(File cacheFile, String path,
			String name, String suffix) {
		// Attachments are never cached because they can change in the database
		// (someone can implement a database refresh method if needed)
		String fullSuffix = suffix != null ? "." + suffix : "";
		String fullPath = (path!=null&&path.length()>0? path+ "/":"") + name + fullSuffix;
		if(record==null)
			throw new AdempiereException("no record choosen for report attachment");
		MAttachment attachment = record.getAttachment();
		if(attachment==null)
			return null;
		MAttachmentEntry[] entries = attachment.getEntries();
		for (MAttachmentEntry entry : entries) {
			if(entry.getName().equals(fullPath)){
				return false;
			}
		}
		if(parentFileResover instanceof ReportFileResolver){
			return ((ReportFileResolver)parentFileResover).checkCacheFreshness(cacheFile, path, name, suffix);
		}
		// unknown resolver type: It is the surest not to cache that
		return null;
	}

	@Override
	protected InputStream loadOriginalFileAsStream(String path, String name,
			String suffix) {
		String fullSuffix = suffix != null ? "." + suffix : "";
		String fullPath = (path!=null&&path.length()>0? path+ "/":"") + name + fullSuffix;
		if(record==null)
			throw new AdempiereException("no record choosen for report attachment");
		MAttachment attachment = record.getAttachment();
		if(attachment==null)
			return null;
		MAttachmentEntry[] entries = attachment.getEntries();
		for (MAttachmentEntry entry : entries) {
			if(entry.getName().equals(fullPath)){
				ByteArrayInputStream strm = new ByteArrayInputStream(entry.getData());
				log.warning("loading file from attachment: " + fullPath);
				return strm;
			}
		}
		return null;
	}

}
